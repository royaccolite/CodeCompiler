import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { QuestionService } from '../test/services/question.service';
import { SharedService } from '../test/services/shared.service';
import { LoginService } from '../services/login.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Location } from '@angular/common';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
  providers: [QuestionService]
})
export class LoginComponent {
  emailMessage: string = '';
  email:string='';
  candidateId!:number;
  constructor(
    private router: Router ,
    private loginService:LoginService,
    private snackBar: MatSnackBar,
    private location: Location,
    private sharedService:SharedService
    ) {
      this.location.forward();
    }

  login() {
    if (this.email.trim() === '') {
      this.emailMessage = 'Please enter a email !!!';
      return;
    }
    this.loginService.getCandidateIdByEmail(this.email).subscribe(
      (candidateId) => {
        if (candidateId) {
          console.log(candidateId);
          this.sharedService.setUserId(candidateId);
          // this.router.navigate(['/startTest', this.candidateId]);
          this.router.navigate(
            ['/startTest'],
            {queryParams:{candidateId:candidateId}}
          );
        } else {
          this.emailMessage = 'Retry again  !!!';
          this.openSnackBar('Candidate Id is not registered  !!!');
        }
      },
      (error) => {
        console.error('Error checking questions existence:', error);
        this.emailMessage = 'Error checking questions existence. Please try again.';
      }
    );
  }
  clearErrorMessage() {
    this.emailMessage = '';
  }
  openSnackBar(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000, // Duration in milliseconds
      horizontalPosition: 'center',
      verticalPosition: 'top',
    });
  }

}
