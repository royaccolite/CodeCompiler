import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { TestComponent } from './test/test.component';
import { FeedbackComponent } from './feedback/feedback.component';
import { ThankYouComponent } from './thank-you/thank-you.component';

const routes: Routes = [
  {
    path:'',
    component:LoginComponent,
  },
  {
    path:'startTest',
    component: TestComponent
  }, {
    path:'startTest/:candidateId',
    component: TestComponent
  },
  {
    path:'feedback/:candidateId',
    component:FeedbackComponent
  },
  {
    path:'thankyou',
    component:ThankYouComponent
  }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
