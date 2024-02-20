// monaco-editor.service.ts

import { Injectable } from '@angular/core';

declare const monaco: any;

@Injectable({
  providedIn: 'root'
})
export class MonacoEditorService {

  initMonaco(): Promise<void> {
    return new Promise<void>((resolve) => {
      if (typeof (window as any).monaco === 'object') {
        resolve();
        return;
      }

      const onGotAmdLoader = () => {
        console.log('AMD loader loaded');
        // Load Monaco Editor
        (window as any).require.config({ paths: { 'vs': 'assets/monaco-editor/min/vs' } });
        (window as any).require(['vs/editor/editor.main'], () => {
          console.log('Monaco Editor loaded');
          resolve();
        });
      };

      const loaderScript = document.createElement('script');
      loaderScript.type = 'text/javascript';
      loaderScript.src = 'assets/monaco-editor/min/vs/loader.js';
      loaderScript.onload = onGotAmdLoader;
      document.body.appendChild(loaderScript);
    });
  }
}
