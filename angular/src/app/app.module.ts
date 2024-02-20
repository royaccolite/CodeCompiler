
import { NgModule } from '@angular/core';
import {
  BrowserModule,
  provideClientHydration,
} from '@angular/platform-browser';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import {MatSliderModule} from '@angular/material/slider';
import{MatInputModule} from '@angular/material/input'
import { MatSelectModule } from '@angular/material/select';
import{MatRadioModule} from '@angular/material/radio';
import { MatDialogModule } from '@angular/material/dialog';
import {MatStepperModule} from '@angular/material/stepper';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './test/header/header.component';
import { MonacoEditorComponent } from './test/monaco-editor/monaco-editor.component';
import { FormsModule } from '@angular/forms';
import { QuestionListComponent } from './test/question-list/question-list.component';
import { ConsoleComponent } from './test/console/console.component';
import { HttpClientModule } from '@angular/common/http';
import { FeedbackComponent } from './feedback/feedback.component';
import { LoginComponent } from './login/login.component';
import { TestComponent } from './test/test.component';
import { ThankYouComponent } from './thank-you/thank-you.component';
import { WarningDialogComponent } from './warning-dialog/warning-dialog.component';
import { TimerComponent } from './test/timer/timer.component';
import { ExplorerComponent } from './test/explorer/explorer.component';
import { MatTreeModule } from '@angular/material/tree';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    TestComponent,
      ConsoleComponent,
      HeaderComponent,
      MonacoEditorComponent,
      QuestionListComponent,
    FeedbackComponent,
    ThankYouComponent,
    WarningDialogComponent,
    TimerComponent,
    ExplorerComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    BrowserAnimationsModule,

    MatButtonModule,
    MatToolbarModule,
    MatButtonToggleModule,
    MatSidenavModule,
    MatTabsModule,
    MatIconModule,
    MatSliderModule,
    MatInputModule,
    MatSelectModule,
    MatRadioModule,
    MatDialogModule,
    
    HttpClientModule,
    MatButtonModule,
    MatIconModule,
    MatTreeModule,
    MatStepperModule
  ],
  providers: [provideClientHydration()],
  bootstrap: [AppComponent],
})
export class AppModule {}
