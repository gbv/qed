export interface BlobInfo {
  id: () => string;
  name: () => string;
  filename: () => string;
  blob: () => Blob;
  base64: () => string;
  blobUri: () => string;
  uri: () => string | undefined;
}

export interface FileUploadEvent {
  blobInfo: BlobInfo;
  success: (url: string) => void;
  failure: (err: string, options?: { remove?: boolean }) => void;
  progress: (percent: number) => void;
}