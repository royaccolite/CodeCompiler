import { Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import {FileExplorerService } from '../services/explorer.service';
import { FileNode } from '../model/file-node.model';
import { MatTreeFlatDataSource, MatTreeFlattener, MatTreeNestedDataSource } from '@angular/material/tree';
import { FlatTreeControl, NestedTreeControl } from '@angular/cdk/tree';
import { FileContentService } from '../services/file-content.service';
import { SharedService } from '../services/shared.service';
import { log } from 'console';
@Component({
  selector: 'app-explorer',
  templateUrl: './explorer.component.html',
  styleUrl: './explorer.component.scss',
})
export class ExplorerComponent {
  treeControl = new NestedTreeControl<FileNode>(node => node.children);
  dataSource = new MatTreeNestedDataSource<FileNode>();
  
  constructor(private fileExplorerService: FileExplorerService,
    protected fileContentService: FileContentService,
    private sharedService:SharedService) {
    
  }

  ngOnInit(): void {
    this.fileExplorerService.getFileStructure().subscribe(data => {
      this.dataSource.data = data;
      console.log(data);
      
    });
  }

  hasChild = (_: number, node: FileNode) => !!node.children && node.children.length > 0;
  // loadChildren(node: FileNode) {
  //   return this.fileExplorerService.loadChildren(node);
  // }
  loadChildren(node: FileNode) {
    if (node.children && node.children.length === 0) {
      this.fileExplorerService.loadChildren(node).subscribe(children => {
        node.children = children;
        this.dataSource.data = [...this.dataSource.data]; // Trigger a data change to update the view
      });
    }
  }

   // Function to fetch file content
   getFileContent(node: FileNode): void {
    console.log('Node Absolute Path:', node.relativePath);

    this.fileContentService.getFileContent(node.relativePath).subscribe(
      contentMap => {
        // Assuming contentMap is an object with key-value pairs
        Object.entries(contentMap).forEach(([fileName, fileContent]) => {
          console.log(`Content of file is`, fileContent);
          this.sharedService.updateContent(fileContent);
          // console.log(this.sharedService.content$);
          
  
          // If you want to display the content, you can update a component property
          // For example, assuming you have a property named 'fileContent' in your component
          // this.fileContent = fileContent;
  
          // Alternatively, you can display it in a modal or any other UI element
        });
      },
      error => {
        console.error('Error fetching file content:', error);
      }
    );
  }


  // Function to handle file click
  onFileClick(node: FileNode): void {
    if (node.isFile) {
      this.getFileContent(node);
    }
  }
}
