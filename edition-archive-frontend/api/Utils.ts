export function pad(num: number, size: number) {
  let numStr = num.toString();
  while (numStr.length < size) numStr = "0" + numStr;
  return numStr;
}

function* trimStringTokens(abstract: string) {
  const regex = /(?<content>[^, .!?]+)(?<seperator>[, .!?]*)/g;

  let m: any;
  while ((m = regex.exec(abstract)) !== null) {
    if (m.index === regex.lastIndex) {
      regex.lastIndex++;
    }
    yield {content: m?.groups?.content, sep: m?.groups?.seperator}
  }
}

export function trimString(str: string | null, maxLength = 200): string | null {
  if (str == null) {
    return null;
  }
  let tokens = trimStringTokens(str);
  let count = 0;
  let next;
  let result = "";
  let lastSep = "";
  while ((next = tokens.next().value) != null) {
    const {content, sep} = next;
    count += content.length + sep.length;

    if (count > maxLength) {
      if(result.length === 0){
        // the first token is already too long. Just return the first 200 characters
        return str.substring(0, maxLength);
      } else {
        return result + "…";
      }
    }
    // the text continues to fit. Add the token to the result
    result += lastSep;
    result += content;

    // remember the last separator, so we can add it to the next token.
    lastSep = sep;
  }
  return result;
}

export function partialEscapeSpecialChars(s: string) {
  return s
    .replace(/([\+\-\(\)\{\}\[\]\^\:\\\/])/g, function (match) {
      return '\\' + match;
    })
    .replace(/&&/g, '\\&\\&')
    .replace(/\|\|/g, '\\|\\|');
}

export function escapeSpecialChars(s: string) {
  return s
    .replace(/([\+\-!\(\)\{\}\[\]\^"~\*\?:\\\/])/g, function (match) {
      return '\\' + match;
    })
    .replace(/&&/g, '\\&\\&')
    .replace(/\|\|/g, '\\|\\|');
}