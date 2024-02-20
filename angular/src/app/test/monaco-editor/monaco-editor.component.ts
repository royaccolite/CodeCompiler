import { AfterViewInit, Component, ElementRef, EventEmitter, Inject, Input, OnDestroy, OnInit, Output, PLATFORM_ID, Renderer2, ViewChild } from '@angular/core';
import { MonacoEditorService } from '../services/monaco-editor.service';
import { Question } from '../model/question.model';
import { SharedService } from '../services/shared.service';
import { FormsModule } from '@angular/forms';
import { HttpHeaders } from '@angular/common/http';
import { SubmitService } from '../services/submit.service';
import { isPlatformBrowser } from '@angular/common';
import { Subscription } from 'rxjs';
import { MonacoEditorCommunicationService } from '../services/monaco-editor-communication.service';

declare const monaco: any;

@Component({
  selector: 'app-monaco-editor',
  templateUrl: './monaco-editor.component.html',
  styleUrl: './monaco-editor.component.scss',
})
export class MonacoEditorComponent implements OnInit, OnDestroy {
  @ViewChild('editorContainer') editorContainer!: ElementRef;

  @Output() languageChanged = new EventEmitter<string>();
  @Output() contentFromLocalStorage = new EventEmitter<string>();

  customInput!: string;
  @Input() questionId!: number;
  @Input() candidateId!: number;
  @Input() selectedLanguage: string = 'cpp';
  @Input() consoleContent: string = ''; // Add this line
  private monacoInitialized: boolean = false;
  testcases: string[] = [
    'TestCase 1',
    'TestCase 2',
    'TestCase 3',
    'TestCase 4',
    'TestCase 5',
    'TestCase 6',
    'TestCase 7',
    'TestCase 8',
    'TestCase 9',
    'TestCase 10',
  ];
  matched: boolean[] = [];
  // customInput: string = '';
  customOutput: string = '';
  results: string[] = [];
  runResults: string[] = [];
  sqlResults:Object[]=[];
  sqlRunResults:Object[]=[];
  selectedQuestionId!: number;

  selectedQuestion: Question | null = null;
  private editor: any;
  selectedTheme: string = 'vs-dark';
  isFullScreen: boolean = false;
  userId!: number;

  @Output() codeChanged = new EventEmitter<string>();

  subscription!: Subscription;
  editorContent: string = '';

  constructor(
    private monacoService: MonacoEditorService,
    private sharedService: SharedService,
    private submitService: SubmitService,
    private monacoEditorCommunicationService: MonacoEditorCommunicationService,
    @Inject(PLATFORM_ID) private platformId: Object,
    private el: ElementRef,
    private renderer: Renderer2
  ) {}
  ngOnInit() {
    if (isPlatformBrowser(this.platformId))  {
        // this.sharedService.selectedLanguage$.subscribe((language) => {  // Subscribe to language changes
        //   this.selectedLanguage = language;
        // });
      this.sharedService.getData$().subscribe((data) => {
        this.selectedQuestion = data[0];
        this.monacoService.initMonaco().then(() => {
          // Load Java and C++ language support extensions
          (window as any).require(
            [
              'vs/basic-languages/java/java',
              'vs/basic-languages/cpp/cpp',
              'vs/basic-languages/python/python',
              'vs/basic-languages/mysql/mysql',
            ],
            () => {
              this.setupMonacoEditor();
            }
          );
        });

        //this.loadContentFromLocalStorage();
      });
    }
    this.sharedService.customInput$.subscribe((customInput) => {
      this.customInput = customInput;
    });
    this.subscription = this.sharedService.content$.subscribe((content) => {
      console.log('Received content from shared service:', content);
      this.editorContent = content;
      this.updateEditorContent(content); // Update the editor content
    });
  }
  templatesExist(): boolean {
    return !!this.selectedQuestion && this.selectedQuestion.templates.length > 0;
  }

  ngOnDestroy() {
    // if (this.editor && this.readOnlyDecorationId) {
    //   this.editor.deltaDecorations(this.readOnlyDecorationId, []);
    // }
    this.disposeEditor();
    //this.loadCodeFromLocalStorage();
  }

  setupMonacoEditor() {
    // Register Java language
    monaco.languages.register({
      id: 'java',
      extensions: ['.java'],
      aliases: ['Java', 'java'],
      mimetypes: ['text/x-java-source'],
    });

    // Register C++ language
    monaco.languages.register({
      id: 'cpp',
      extensions: ['.cpp', '.h'],
      aliases: ['C++', 'cpp'],
      mimetypes: ['text/x-c++src', 'text/x-c++hdr'],
    });
    monaco.languages.register({
      id: 'python',
      extensions: ['.py'],
      aliases: ['Python', 'python'],
      mimetypes: ['text/x-python'],
    });
    monaco.languages.register({
      id: 'mysql',
      extensions: ['.sql'],
      aliases: ['MySQL', 'mysql'],
      mimetypes: ['text/x-mysql'],
    });

    // Create the editor with Java and C++ support
    this.editor = monaco.editor.create(this.editorContainer.nativeElement, {
      value: this.getInitialCode(),
      language: this.selectedLanguage,
      theme: this.selectedTheme,
      // lineNumbers:'on',
      readOnly: false,
      // selectionClipboard: false,
    });
    const boilerplateCode = this.getInitialCode();
    console.log(this.editor.getValue());
    
    // Set up listeners or other configurations if needed
    this.setupEditorListeners();
  }

  setupEditorListeners() {
    // Example: Listen for editor content change
    this.editor?.onDidChangeModelContent(() => {
      this.updateCodeInSharedService();
      this.saveCodeToLocalStorage();
    });
    this.editor?.onKeyDown((event: { preventDefault?: any; keyCode?: any; ctrlKey?: any; metaKey?: any; }) => {
      const { keyCode, ctrlKey, metaKey } = event;
      if ((keyCode === 33 || keyCode === 52) && (metaKey || ctrlKey)) {
        event.preventDefault();
        }
      }
    );

  }

  saveCodeToLocalStorage() {
    const currentCode = this.editor?.getValue() || '';
    const key = `editorContent_${this.selectedLanguage}_${this.selectedQuestion?.id}`;
    localStorage.setItem(key, currentCode);
  }

  onLanguageChange() {
    // this.saveCodeToLocalStorage();
    if (this.editor) {
      this.editor.dispose();
    }
    this.setupMonacoEditor();
    this.updateCodeInSharedService();
    this.sharedService.updateSelectedLanguage(this.selectedLanguage);  // Notify the change
    this.loadContentFromLocalStorage();
    this.saveCodeToLocalStorage();
    this.languageChanged.emit(this.selectedLanguage);
  }

  change(question: Question) {
    this.saveCodeToLocalStorage();
    if (this.editor) {
      this.editor.dispose();
    }

    this.selectedQuestion = question;
    this.setupMonacoEditor();

    if (this.monacoInitialized) {
      this.updateCodeInSharedService();
      this.loadContentFromLocalStorage();
      this.saveCodeToLocalStorage();
    }
  }
  disposeEditor() {
    //this.saveCodeToLocalStorage();
    if (this.editor) {
      this.editor.dispose();
    }
  }
  onThemeChange() {
    if (this.editor) {
      monaco.editor.setTheme(this.selectedTheme);
    }
  }
  toggleFullscreen() {
    const element = this.el.nativeElement;

    // Check if fullscreen class is already applied
    const isFullscreen = element.classList.contains('fullscreen');

    // Toggle the fullscreen class
    if (isFullscreen) {
      this.renderer.removeClass(element, 'fullscreen');
      this.editorContainer.nativeElement.style.height = '365px'; // Set the desired height when not in fullscreen
      this.editorContainer.nativeElement.style.width = '50%'; // Set the desired width when not in fullscreen

      // Show other components
      this.showOtherComponents(true);
    } else {
      this.renderer.addClass(element, 'fullscreen');
      this.editorContainer.nativeElement.style.height = '100vh'; // Set the height to full viewport height
      this.editorContainer.nativeElement.style.width = '100%'; // Set the width to full viewport width

      // Hide other components
      this.showOtherComponents(false);
    }
  }

  private showOtherComponents(show: boolean) {
    // If you have other components, toggle their visibility
    // Example: this.consoleComponent.nativeElement.style.display = show ? 'block' : 'none';
    // Example: this.descriptionComponent.nativeElement.style.display = show ? 'block' : 'none';
  }

  exitFullScreen() {
    this.isFullScreen = false;
    document.exitFullscreen();
    this.editor?.layout();
  }

  getInitialCode(): string {
    if (this.selectedLanguage === 'java') {
      return (
        this.selectedQuestion?.templates.find(
          (data) => data.language === 'Java'
        )?.code || ''
      );
    } else if (this.selectedLanguage === 'cpp') {
      return this.selectedQuestion?.templates.find((data) => data.language === 'Cpp')?.code || '';
    }
    else if (this.selectedLanguage === 'python') {
      return this.selectedQuestion?.templates.find((data) => data.language === 'python')?.code || '';
    }
    else {return '// Default code...';
    }
  }

  getCurrentCode(): string {
    if (this.editor) {
      return this.editor.getValue();
    }
    return '';
  }

  updateCodeInSharedService() {
    const currentCode = this.editor?.getValue() || '';
    this.monacoEditorCommunicationService.setCode(currentCode);
    this.codeChanged.emit(currentCode);
    console.log('current code is', currentCode);
    
  }
  
  updateEditorContent(newContent: string) {
    if (this.editor) {
      console.log('Updating editor content:', newContent);
      this.editor.setValue(newContent);
      console.log('new content is ',newContent);
      
    }
  }
  private loadContentFromLocalStorage() {
    const savedContent = localStorage.getItem(
      `editorContent_${this.selectedLanguage}_${this.selectedQuestion?.id}`
    );
    if (savedContent !== null && this.editor) {
      this.editor.setValue(savedContent);
    }
  }
  private submittedForQuestion = new Set<string>();
  onSubmit() {
    const combinationKey = `${this.candidateId}_${this.questionId}`;
    if (!this.submittedForQuestion.has(combinationKey)) {
    this.consoleContent = this.editor?.getValue() || '';
    const code = {
      candidateId: this.candidateId,
      questionId: this.questionId,
      submittedCode: this.consoleContent,
      language: this.selectedLanguage,
      customInput: this.customInput == '' ? null : this.customInput,
      submitStatus: true,
    };
    console.log(code);
    console.log('getting the question id', this.questionId);
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    if (code.language.trim().toLowerCase()  === 'mysql') {
  
      this.submitService.submitCode(code).subscribe(
        (results: Object[]) => {
          console.log('Results received:', results);
          this.sqlResults = results;
          this.sharedService.updateSqlResults(this.results);
 
        }
      )
    }
    else{
      this.submitService.submitCode(code).subscribe(
        (results: string[]) => {
          this.results = results;
          this.sharedService.updateResults(this.results);
          this.matched = this.results.map((result) =>
            result.includes('Successful')
          );
          this.submittedForQuestion.add(combinationKey);
        },
        (error) => {
          console.log('Error:', error);
        }
      );
    }
  }
  
  else {
    alert('Already submitted for this combination.');
    console.log('Already submitted for this combination.');
    // Optionally, you can display a message indicating that the submission has already occurred.
  }
}

  public checkTestCase(test: string): boolean {
    return test.toLowerCase().includes('output');
  }

  onRun() {
    this.consoleContent = this.editor?.getValue() || '';
    const code = {
      candidateId: this.candidateId,
      questionId: this.questionId,
      submittedCode: this.consoleContent,
      language: this.selectedLanguage,
      customInput: this.customInput ? this.customInput : null,
      submitStatus: false,
    };
    this.runResults = [];
    this.sqlRunResults=[];
    this.sharedService.updateRunResults(this.runResults);

    console.log('output is refreshed!');
    console.log(code);

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    
    if (code.language.trim().toLowerCase()  === 'mysql') {
      this.sqlRunResults=[];
      const headers = new HttpHeaders({
        'Content-Type': 'application/json',
      });
  
      this.submitService.submitCode(code).subscribe(
        (results: Object[]) => {  
          console.log('Results received:', results);
    
          this.sqlRunResults = results;
          this.sharedService.updateSqlRunResults(this.sqlRunResults);
        }
      )

    }
    else
    {
    this.submitService.submitCode(code).subscribe(
      (results: string[]) => {
        console.log('Results received:', results);
        this.runResults = results;
        this.sharedService.updateRunResults(this.runResults);
        this.matched = this.runResults.map((result) =>
          result.includes('Successful')
        );
      },
      (error) => {
        console.log('Error:', error);
        // Display error message or perform actions for submission error
      }
    );
  }}
  
}

function onRightClick(event: Event | undefined, MouseEvent: { new(type: string, eventInitDict?: MouseEventInit | undefined): MouseEvent; prototype: MouseEvent; }) {
  throw new Error('Function not implemented.');
}

