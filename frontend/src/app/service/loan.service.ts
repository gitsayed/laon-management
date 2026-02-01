import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseService } from "./base-services";
import { environment } from '../../environments/environment.development';

export interface Loan {
  id: number;
  customerName: string;
  principalAmount: number;
  interestRate: number;
  tenureMonths: number;
  status: string;
  emiAmount?: number;
  totalPaid?: number;
  remainingBalance?: number;
}

export interface Payment {
  loanId: number;
  amountPaid: number;
  paymentDate: string | null;
}

export interface LoanCreate {
  customerName: string,
  principalAmount: number,
  interestRate: number,
  tenureMonths: number,
  createdDate: string|null
}

const  baseUrl = environment.apiBaseUrl;

@Injectable({
  providedIn: 'root'
})
export class LoanService extends BaseService {
 

  constructor(private http: HttpClient) {
    super();
  }

  fetchPagedLoan(map: Map<string, any>): Observable<Loan[]> {
    let params = this.mapToHttpParams(map);
    return this.http.get<Loan[]>(`${baseUrl}/loans${params}`);
  }

  createLoan(loan: LoanCreate): Observable<any> {
    return this.http.post<any>(`${baseUrl}/loans`, loan);
  }

  addPayment(payment: Payment): Observable<Payment> {
    return this.http.post<Payment>(`${baseUrl}/payments`, payment);
  }

  getLoanSummary(id: number): Observable<Loan> {
    return this.http.get<Loan>(`${baseUrl}/loans/${id}/summary`);
  }

  
}
