export interface FileNode {
  name: string;
  relativePath: string;
  isFile: boolean;
  children?: FileNode[];
  
}
