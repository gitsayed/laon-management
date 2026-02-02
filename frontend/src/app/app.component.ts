import { AfterViewInit, Component, inject, OnInit, ViewChild } from '@angular/core';

import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { LoanService } from './../app/service/loan.service';
import { CommonModule } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { LoanSummaryComponent } from './loan-summary/loan-summary.component';
import { LoanCreateComponent } from './loan-create/loan-create.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  standalone: false,
})
export class AppComponent implements AfterViewInit, OnInit {
  displayedColumns: string[] = ['id', 'customerName', 'principalAmount', 'interestRate',
    'tenureMonths', 'emiAmount', 'createdDate', 'nextDueDate', 'status'];
  dataSource = new MatTableDataSource<PeriodicElement>([]);
  totalItems = 0;
  page = 0;
  size = 10;


  @ViewChild(MatPaginator) paginator!: MatPaginator;
  readonly dialog = inject(MatDialog);

  constructor(private loanService: LoanService) { }


  ngOnInit() {
    this.dataSource.paginator = this.paginator;
  }

  ngAfterViewInit() {
    this.loadLoans();
  }

  loadLoans(map?: Map<string, any>) {
    map = map ? map : new Map();
    map.set('page', this.page);
    map.set('size', this.size);
    this.loanService.fetchPagedLoan(map).subscribe({next:(response: any) => {
      this.dataSource.data = response.content;
      this.totalItems = response.totalElements;
    },
    error: err=> {
      console.log(err.message);
      
    }
  });

  }

  onRowClick(event: any) {
    const dialogRef = this.dialog.open(LoanSummaryComponent, {
      data: { loanId: event.id },
      autoFocus: true,
      width: '50%',
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log("afterClosed :", result);
      this.ngAfterViewInit();
    });

  }


  createNewLoan(event: any) {
    console.log('createNewLoan ', event);
    const dialogRef = this.dialog.open(LoanCreateComponent, {
      data: { loanId: null },
      autoFocus: true,
      width: '50%',
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log("afterClosed :", result);
      this.ngAfterViewInit();
    });

  }

  onPageChange(event: PageEvent): void {
    this.page = event.pageIndex;
    this.size = event.pageSize
    this.loadLoans();
  }




}




export interface PeriodicElement {
  name: string;
  position: number;
  weight: number;
  symbol: string;
}




