import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { LoanCreate, LoanService } from '../service/loan.service';

@Component({
  selector: 'app-loan-create',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatDatepickerModule,
    MatNativeDateModule
  ],
  templateUrl: './loan-create.component.html',
  styleUrls: ['./loan-create.component.scss']
})
export class LoanCreateComponent implements OnInit {

  readonly dialogRef = inject(MatDialogRef<LoanCreateComponent>);
  readonly data = inject<any>(MAT_DIALOG_DATA);
  loanForm: FormGroup;
    loading:boolean = false;
    error: any = '';
  constructor(private fb: FormBuilder, 
    private datePipe: DatePipe,
    private loanService: LoanService) {

  }

  ngOnInit(): void {
    this.loanForm = this.fb.group({
      customerName: ['', [Validators.required, Validators.minLength(3)]],
      principalAmount: [null, [Validators.required, Validators.min(1000)]],
      interestRate: [null, [Validators.required, Validators.min(1), Validators.max(100)]],
      tenureMonths: [null, [Validators.required, Validators.min(1)]],
      createdDate: [null, Validators.required],
    });
  }



  onSubmit() {
    if (this.loanForm.valid) {
      console.log('✅ Loan Form Submitted:', this.loanForm.value);
      let payload : LoanCreate = this.loanForm.value;
      payload.createdDate = this.datePipe.transform(payload.createdDate, 'yyyy-MM-dd')
       this.loading = false;
      this.loanService.createLoan(payload).subscribe(res=> {
           this.loading = false;
        this.dialogRef.close({ updated: true });
        
      });

    } else {
      this.loanForm.markAllAsTouched();
    }
  }

  get f() {
    return this.loanForm.controls;
  }
}
