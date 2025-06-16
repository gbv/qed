set -e

action=$1
email=$2
password=$3
schema_host=$4
target_dir=$5

# count arguments and print usage if not enough
if [ $# -lt 5 ]; then
  echo "Usage: $0 action email password schema_host target_dir"
  echo "Example: $0 backup admin@gbv.de test1234 http://localhost:8000 ./export_tmp"
  exit 1
fi

# check if jq is installed
if ! [ -x "$(command -v jq)" ]; then
  echo 'Error: jq is not installed.' >&2
  exit 1
fi

# check if curl is installed
if ! [ -x "$(command -v curl)" ]; then
  echo 'Error: curl is not installed.' >&2
  exit 1
fi

# check if action is backup or restore
if [ "$action" != "backup" ] && [ "$action" != "restore" ]; then
  echo "Error: action must be backup or restore." >&2
  exit 1
fi

# check if email is provided
if [ -z "$email" ]; then
  echo "Error: email is not provided." >&2
  exit 1
fi

# check if password is provided
if [ -z "$password" ]; then
  echo "Error: password is not provided." >&2
  exit 1
fi

# check if schema_host is provided
if [ -z "$schema_host" ]; then
  echo "Error: schema_host is not provided." >&2
  exit 1
fi

# check if target_dir is provided
if [ -z "$target_dir" ]; then
  echo "Error: target_dir is not provided." >&2
  exit 1
fi

# check if target_dir exists
if [ ! -d "$target_dir" ]; then
  echo "Error: target_dir does not exist." >&2
  exit 1
fi

# check if target_dir is writable
if [ ! -w "$target_dir" ]; then
  echo "Error: target_dir is not writable." >&2
  exit 1
fi

# login and get access token
login_result=$(curl -X POST -d "{\"email\": \"${email}\", \"password\": \"$password\"}" -H 'Content-Type: application/json' "${schema_host}/auth/login/");
token=$(echo "$login_result" | jq -r '.data.access_token')

# check if token is empty
if [ -z "$token" ]; then
  echo "Error: token is empty." >&2
  exit 1
fi

# check if action is backup
if [ "$action" = "backup" ]; then

echo "Exporting schemas..."
# get all schemas
curl -X GET -H "Authorization: Bearer ${token}" "${schema_host}/schema/snapshot?export=json" > "${target_dir}/schemas.json"

echo "Exporting collections..."

echo "Exporting page translations..."
curl -X GET -H "Authorization: Bearer ${token}" "${schema_host}/items/Page_translations?fields=Page_id,content,id,languages_code" | jq ".data" > "${target_dir}/Page_translations.json"

echo "Exporting pages..."
curl -X GET -H "Authorization: Bearer ${token}" "${schema_host}/items/Page?fields=id,status,slug,project" | jq ".data" > "${target_dir}/Page.json"

echo "Exporting languages..."
curl -X GET -H "Authorization: Bearer ${token}" "${schema_host}/items/languages?fields=code,name,direction" | jq ".data" > "${target_dir}/languages.json"

echo "Exporting files..."
# get all files
file_result=$(curl -X GET -H "Authorization: Bearer ${token}" "${schema_host}/files/")

# extract file ids and names
file_ids=$(echo "$file_result" | jq -r ".data[] | .id + \"_splitme_\" + .filename_download" | grep -v "^$")

mkdir -p "${target_dir}/files"

# loop over all files
while read file; do
  # get file id and name
  file_id=$(echo "$file" | sed -r 's/(.+)_splitme_.*/\1/')

  # download file
  curl -X GET -H "Authorization: Bearer ${token}" "${schema_host}/assets/${file_id}" > "${target_dir}/files/${file}"
done <<< "$file_ids"

echo "Done."
fi

# check if action is restore
if [ "$action" = "restore" ]; then

echo "Importing schemas..."
# get schema difference
diff=$(curl -X POST -H "Authorization: Bearer ${token}" -H 'Content-Type: application/json' -d @${target_dir}/schemas.json "${schema_host}/schema/diff")


# check if diff is empty or if update is needed
if [ -z "$diff" ] || [ "$diff" = "[]" ]; then
  echo "No schema update needed."
else
  echo "Updating schemas..."
  curl -X POST -H "Authorization: Bearer ${token}" -H 'Content-Type: application/json' -d "$(echo "$diff"|jq ".data")" "${schema_host}/schema/apply"
fi

# import all collections
collection_names=("languages" "Page" "Page_translations")

echo "Importing collections..."
# loop over collections_names
for collection in "${collection_names[@]}"; do
  echo "Importing ${collection}..."
  echo "------------------"
  curl -X POST -H "Authorization: Bearer ${token}" -H 'Content-Type: application/json' -d @${target_dir}/${collection}.json "${schema_host}/items/${collection}"
done

echo "Importing files..."
# get all files
file_id_names=$(ls -1 ${target_dir}/files)

# loop over all files
while read file_id_name; do
  file_id=$(echo "$file_id_name" | sed -r 's/(.+)_splitme_.*/\1/')
  file_name=$(echo "$file_id_name" | sed -r 's/.+_splitme_(.+)/\1/')
  echo "Importing file ${file_name} with id ${file_id}..."
  curl -X POST -H "Authorization: Bearer ${token}" -F "title=${file_name}" -F "id=${file_id}" -F "file=@${target_dir}/files/${file_id_name}" "${schema_host}/files"
done <<< "$file_id_names"


fi
