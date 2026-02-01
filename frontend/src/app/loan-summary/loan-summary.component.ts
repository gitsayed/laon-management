import { Component, inject, model } from "@angular/core";
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogClose, MatDialogContent, MatDialogRef, MatDialogTitle } from "@angular/material/dialog";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { LoanService, Payment } from "../service/loan.service";
import { CommonModule, DatePipe } from "@angular/common";
import { BrowserModule } from "@angular/platform-browser";
import { MatCardModule } from "@angular/material/card";
import { MatDividerModule } from "@angular/material/divider";
import { MatDatepickerModule } from "@angular/material/datepicker";




@Component({
  selector: 'loan-summary',
  templateUrl: 'loan-summary.component.html',
  styleUrl: './loan-summary.component.scss',
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    MatButtonModule,
    MatCardModule,
    MatDividerModule,
    MatDatepickerModule,
    ReactiveFormsModule

  ],
})
export class LoanSummaryComponent {
  readonly dialogRef = inject(MatDialogRef<LoanSummaryComponent>);
  readonly data = inject<any>(MAT_DIALOG_DATA);
  loanId: number = this.data.loanId;
  summary: any | null = null;
  loading: boolean = false;
  error: string = '';
  paymentForm: FormGroup;
  formActive: boolean = false;

  constructor(private formBuilder: FormBuilder,
    private datePipe: DatePipe,
    private loanService: LoanService
  ) {

  }


  ngOnInit(): void {

    if (this.loanId) {
      this.fetchLoanSummary(this.loanId);
    }
  }

  fetchLoanSummary(id: number): void {
    this.loading = true;
    this.loanService.getLoanSummary(id).subscribe({
      next: (data) => {
        this.summary = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load Loan Summary.';
        console.error(err);
        this.loading = false;
      }
    });
  }

  emiPayment(event: any) {
    console.log('emiPayment', event);

    this.formActive = true;
    this.formInit();
  }


  formInit(): void {
    this.paymentForm = this.formBuilder.group({
      loanId: [this.loanId, Validators.required],
      amountPaid: [this.summary.emiAmount , [Validators.required, Validators.min(1)]],
      paymentDate: [null, Validators.required],
    });
  }


  submit() {
    if (this.paymentForm.valid) {
      console.log('Payment data:', this.paymentForm.value);
      let payload: Payment = {
        loanId: this.paymentForm.value.loanId,
        amountPaid: this.paymentForm.value.amountPaid,
        paymentDate: this.datePipe.transform(this.paymentForm.value.paymentDate, 'yyyy-MM-dd')
      }
      this.loading = true;
      this.loanService.addPayment(payload).subscribe(res => {
        this.loading = false;
        this.dialogRef.close({ updated: true });
      })

    } else {
      this.paymentForm.markAllAsTouched();
    }
  }
}