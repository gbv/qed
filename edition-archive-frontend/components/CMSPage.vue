<template>
  <div>
    <client-only>
      <div class="btn-block d-flex flex-nowrap flex-row justify-content-end">
        <div v-if="model.authHeader && !model.edit" class="btn-group" role="group">
          <button class="btn btn-primary" type="button" @click="editPage"><i class="bi bi-pencil-square"> </i>
            {{ isNewTranslation ? $t("cms.page.newTranslation") : $t("cms.page.editTranslation") }}
          </button>
          <button v-if="model.page?.status === 'published'"
                  class="btn btn-warning"
                  @click="draftPage"
          >
            <i class="bi bi-feather"> </i>
            {{ $t("cms.page.draft") }}
          </button>
          <button v-else-if="model.page?.status === 'draft'"
                  class="btn btn-success"
                  @click="publishPage"
          >
            <i class="bi bi-check2-circle"> </i>
            {{ $t("cms.page.publish") }}
          </button>
          <button v-if="!isNewTranslation" class="btn btn-danger" type="button" @click="deletePage"><i
            class="bi bi-trash"> </i>
            {{ $t("cms.page.translation.delete") }}
          </button>
        </div>
      </div>
    </client-only>
    <div v-if="model">
      <div v-if="model.loading">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">{{ $t("cms.page.loading") }}</span>
        </div>
      </div>
      <div v-else-if="!model.edit && currentTranslation!=null">
        <DynamicCompiled :template="currentTranslation.content"/>
      </div>
      <div v-else-if="currentTranslation!=null">
        <client-only>
          <CMSPageEditor :content="currentTranslation.content"
                         v-on:cancel="stopEdit"
                         v-on:fileUpload="fileUpload"
                         v-on:save="saveContent"
          />
        </client-only>
      </div>
    </div>

  </div>
</template>

<script lang="ts" setup>
import {createError} from "h3";
import {useUserStore} from "~/store/UserStore";
import {type FileUploadEvent} from "~/api/TinyMCEHelper";
import {refreshLogin} from "~/api/refreshLogin";


const userStore = useUserStore();
const {$directusURL} = useNuxtApp();
const props = defineProps(['slug']);

const i18n = useI18n();

interface PageTranslation {
  id?: number,
  Page_id: number,
  languages_code: string,
  content: string,
}

interface Page {
  date_created: string|null,
  date_updated: string|null,
  id: number,
  project: string,
  slug: string,
  status: "published" | "draft" | "archived",
  user_created: string|null,
  user_updated: string|null,
  contents: Array<number>,
}

interface FileObject {
  id: string,
  storage: string,
  filename_disk: string,
  filename_download: string,
  title: string,
  type: string,
  filesize?: number,
  width?: number,
  height?: number,
  duration?: number,
  description?: string,
  location: string,
}

const model = reactive<{
  contentRequest: any,
  pageRequest: any,
  authHeader: any,
  edit: boolean,
  translations: Array<PageTranslation> | null,
  loading: boolean,
  page: Page | null,

}>({
  contentRequest: null,
  pageRequest: null,
  authHeader: null,
  edit: false,
  translations: null,
  loading: false,
  page: null
});


const getMatchingTranslation = () => {
  if (model.translations == null) {
    return null;
  }
  return model.translations.find(t => {
    if(!t) {
      return false;
    }
    if(!t.languages_code) {
      return false;
    }
    return t.languages_code.startsWith(i18n.localeProperties.value.code+"") ||
    t.languages_code === i18n.localeProperties.value.code;
  }) as PageTranslation || null;
}

const currentTranslation = computed(() => {
  if (model.translations != null) {
    const matchingTranslation = getMatchingTranslation();
    if (matchingTranslation != null) {
      return matchingTranslation;
    }
    return model.translations[0] as PageTranslation;
  } else {
    return null;
  }
});

const isNewTranslation = computed(() => {
  return (getMatchingTranslation()?.id || null) == null;
});

// only execute if on client
model.authHeader = userStore.accessToken ? {Authorization: `Bearer ${userStore.accessToken}`} : null;

const saveContent = async (content: string) => {
  console.log("saveContent", currentTranslation.value);
  if (currentTranslation.value == null) {
    return;
  }
  currentTranslation.value.content = content;
  const isNewTranslation = currentTranslation.value.id == undefined;
  const idPathPart = !isNewTranslation ? "/" + currentTranslation.value.id : "";
  const saveRequest = await useFetch(`${$directusURL()}items/Page_translations${idPathPart}`, {
    method: isNewTranslation ? 'POST' : 'PATCH',
    headers: {
      'Content-Type': 'application/json',
      ...model.authHeader,
    },
    body: JSON.stringify(
      currentTranslation.value
    ),
  });

  if (saveRequest.error.value) {
    console.log(saveRequest.error.value);
    showError(saveRequest.error.value);
    return;
  }

  if (isNewTranslation) {
    currentTranslation.value.id = (saveRequest.data.value as any).data.id;
    currentTranslation.value.content = (saveRequest.data.value as any).data.content;
    //model.translations?.push(currentTranslation.value);
  }
  console.log("saveRequest", saveRequest.data.value);
  stopEdit();
};

const deletePage = async () => {
  if (confirm(i18n.t("cms.page.translation.confirmDelete"))) {
    await deletePageConfirmed();
  }
}
const deletePageConfirmed = async () => {
  if (model.page == null || currentTranslation.value == null || currentTranslation.value.id == undefined || model.translations == null) {
    return;
  }

  const deleteRequest = await useFetch(`${$directusURL()}items/Page_translations/${currentTranslation.value.id}`, {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
      ...model.authHeader,
    },
  });

  if (deleteRequest.error.value) {
    console.log(deleteRequest.error.value);
    showError(deleteRequest.error.value);
    return;
  }

  model.translations = model.translations.filter(t => !currentTranslation.value || t.id !== currentTranslation.value.id);

  console.log("deleteRequest", deleteRequest.data.value);
  stopEdit();
}

const draftPage = async () => {
  if (model.page == null) {
    return;
  }

  const draftRequest = await useFetch(`${$directusURL()}items/Page/${model.page.id}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
      ...model.authHeader,
    },
    body: JSON.stringify(
      {
        status: 'draft'
      }
    ),
  });

  if (draftRequest.error.value) {
    console.log(draftRequest.error.value);
    showError(draftRequest.error.value);
    return;
  }

  model.page.status = 'draft';
  console.log("draftRequest", draftRequest.data.value);
}

const publishPage = async () => {
  if (model.page == null) {
    return;
  }

  const publishRequest = await useFetch(`${$directusURL()}items/Page/${model.page.id}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
      ...model.authHeader,
    },
    body: JSON.stringify(
      {
        status: 'published'
      }
    ),
  });

  if (publishRequest.error.value) {
    console.log(publishRequest.error.value);
    showError(publishRequest.error.value);
    return;
  }

  model.page.status = 'published';
  console.log("publishRequest", publishRequest.data.value);
}

watch(i18n.locale, async () => {
  stopEdit();
  model.translations = await loadPageAndTranslations(props.slug);
});

const editPage = () => {
  if (model.translations == null || model.page == null ) {
    return;
  }
  const matchingTranslation = getMatchingTranslation();
  if (matchingTranslation == null) {
    const newTranslation = {
      Page_id: model.page.id,
      languages_code: i18n.localeProperties.value.code.toString(),
      content: `<div><h3>${i18n.t('cms.page.translation.new')}</h3></div>`,
    };
    model.translations.push(newTranslation);
  }

  model.edit = true;
}

const stopEdit = () => {
  model.edit = false;
  if (model.translations == null) {
    return;
  }
  model.translations = model.translations.filter(t => (t.id || null) != null);
}

const fileUpload = async (fileUploadRequest: FileUploadEvent) => {

  const blob = fileUploadRequest.blobInfo.blob();

  const formData = new FormData();
  formData.append('title', fileUploadRequest.blobInfo.filename());
  formData.append('file', blob);


  const request = await useFetch(`${$directusURL()}files`, {
    method: 'POST',
    headers: {
      ...model.authHeader,
    },
    body: formData,
  });

  if (request.error.value) {
    fileUploadRequest.failure(request.error.value.message, {remove: true});
    return;
  }

  const result = (request.data.value as any).data as FileObject;
  fileUploadRequest.success(`${$directusURL()}assets/${result.id}`);
}

const loadPageAndTranslations = async (slug:string) => {
  model.loading = true;
  model.pageRequest = await useFetch(`${$directusURL()}items/Page?filter={"slug":"${slug}"}`, {key: slug});

  if (!(model.pageRequest?.data?.data?.length > 0)) {
    showError(
      createError({
        statusCode: 404,
        statusMessage: 'Not Found',
      })
    );
    return null;
  }
  let page = model.pageRequest.data.data[0];

  model.page = page;

  if (!(page.status === 'published' || model.authHeader)) {
    showError(
      createError({
        statusCode: 403,
        statusMessage: 'No Rights to access this page',
      })
    );
    return null;
  }

  model.contentRequest = await useFetch(`${$directusURL()}items/Page_translations?filter={"Page_id":${model.page?.id}}`,
    {key: `${props.slug}-translation`});

  if (!(model.pageRequest?.data?.data?.length > 0)) {
    showError(
      createError({
        statusCode: 404,
        statusMessage: 'Not Found',
      })
    );
    return null;
  }

  model.loading = false;
  return model.contentRequest.data.data as Array<PageTranslation>;
};


// on load
model.translations = await loadPageAndTranslations(props.slug);

if (process.client){
  let interval:number|null = null;

  onMounted(() => {
    interval = window.setInterval(()=> {
        refreshLogin();
        model.authHeader = userStore.accessToken ? {Authorization: `Bearer ${userStore.accessToken}`} : null;
      },
      1000 * 60 ); // refresh every minute
  });

  onUnmounted(()=> {
    if(interval){
      clearInterval(interval);
    }
  });

}

</script>

<style scoped>

</style>