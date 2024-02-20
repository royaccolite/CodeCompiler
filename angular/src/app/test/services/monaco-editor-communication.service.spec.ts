import { TestBed } from '@angular/core/testing';

import { MonacoEditorCommunicationService } from './monaco-editor-communication.service';

describe('MonacoEditorCommunicationService', () => {
  let service: MonacoEditorCommunicationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MonacoEditorCommunicationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
