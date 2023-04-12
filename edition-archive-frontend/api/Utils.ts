export function pad(num: number, size: number) {
  let numStr = num.toString();
  while (numStr.length < size) numStr = "0" + numStr;
  return numStr;
}

export function trimString(str: string | null): string | null {
  if (str == null) {
    return null;
  }
  const size = 240;

  if (str.length > size) {
    const trimmed = str.substring(0, size).trim();
    const lastSpaceIndex = trimmed.lastIndexOf(' ');
    return lastSpaceIndex === -1 ? trimmed + "…" : trimmed.substring(0, lastSpaceIndex) + "…";
  } else {
    return str;
  }
}