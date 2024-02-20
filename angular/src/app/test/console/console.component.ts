
import { Component, Input, OnInit } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { SubmitService } from '../services/submit.service';
import { QuestionService } from '../services/question.service';
import { log } from 'console';
import { Observable } from 'rxjs';
import { SharedService } from '../services/shared.service';

@Component({
  selector: 'app-console',
  templateUrl: './console.component.html',
  styleUrls: ['./console.component.scss'],
  providers: [QuestionService]
})

export class ConsoleComponent implements OnInit {
  results: string[] = [];
  runResults: string[] = [];
  sqlResults:String[]=[];
  sqlRunResults:Object[]=[];
  consoleContent: string = '';
  customInput!: string;
  selectedLanguage: string = '';

  constructor(private sharedService: SharedService) { }

  ngOnInit() {
    this.sharedService.results$.subscribe((results) => {
      this.results = results;
  });

  this.sharedService.runResults$.subscribe((runResults) => {
      this.runResults = runResults;
  });

  this.sharedService.consoleContent$.subscribe((consoleContent) => {
      this.consoleContent = consoleContent;
  });
  this.sharedService.customInput$.subscribe((customInput) => {
    this.customInput = customInput;
  });
  this.sharedService.sqlResults$.subscribe((results) => {
    this.sqlResults = results;
 });
 this.sharedService.selectedLanguage$.subscribe((language) => {
  this.selectedLanguage = language;
});

 this.sharedService.sqlRunResults$.subscribe((runResults) => {
    this.sqlRunResults = runResults;
 });
  
   }
  onResultsChanged(results: string[]) {
    this.results = results;
  }

  onRunResultsChanged(runResults: string[]) {
    
    console.log(this.runResults+"skkjsdi");
    this.runResults = runResults;
  }
  onSqlResultsChanged(sqlResults: string[]) {
    this.sqlResults = sqlResults;
  }

  onSqlRunResultsChanged(sqlRunResults: Object[]) {
    this.sqlRunResults = sqlRunResults;
  }
  onCustomInputChange() {
    this.sharedService.updateCustomInput(this.customInput);
    console.log(this.customInput);
  };
  formatTable(data: any[]): string {
    if (!data || data.length === 0) {
      return '';
    }
    
// Check if the input is an array of strings (indicating errors)
if (data.every(item => typeof item === 'string')) {
  // If it's an array of strings (errors), display them without the table structure
  return data.join('<br>');
}
  
    // Extract column headers from the first object in the array
    const headers = Object.keys(data[0]);
  
    // Create the table header
    let table = '<table border="1"><tr>';
    headers.forEach(header => {
      table += '<th>' + header + '</th>';
    });
    table += '</tr>';
  
    // Create rows
    data.forEach(row => {
      table += '<tr>';
      headers.forEach(header => {
        table += '<td>' + row[header] + '</td>';
      });
      table += '</tr>';
    });
  
    table += '</table>';
    return table;
  }
  
}

