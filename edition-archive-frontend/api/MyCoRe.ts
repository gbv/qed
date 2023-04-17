import {pad} from "~/api/Utils";

export function getMyCoReIdNumber(mycoreId?: string) {
  if (mycoreId === null) return null;
  if(mycoreId === undefined) return undefined;
  return parseInt(mycoreId.split("_").pop() as string);
}


export function getMyCoReId(project: string,number: number) {
  return project + "_mods_" + pad(number, 8);
}
