import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Incident } from '../../user/incident';
import { AgentService } from '../agent.service';


@Component({
  selector: 'app-control-panel',
  templateUrl: './control-panel.component.html',
  styleUrls: ['./control-panel.component.css']
})
export class ControlPanelComponent implements OnInit {
  rangeForm!: FormGroup;
  dateError: boolean = false;
  today: string;

  incidentsList !: Incident[];

  constructor(
    private formBuilder: FormBuilder,
    private agentService: AgentService
  ) { 
    const todayDate = new Date();
    this.today = todayDate.toISOString().split('T')[0];
  }

  ngOnInit() {
    this.rangeForm = this.formBuilder.group({
      startDate: ["", Validators.required],
      endDate: ["", Validators.required]
    });

    this.rangeForm.valueChanges.subscribe(() => this.validateDates());
  }

  validateDates() {
    const startDate = this.rangeForm.get('startDate')?.value;
    const endDate = this.rangeForm.get('endDate')?.value;
    this.dateError = startDate && endDate && startDate > endDate;
  }

  searchIncidents() {
    if (this.rangeForm.valid && !this.dateError) {
      const startDate = this.formatDate(this.rangeForm.get('startDate')?.value);
      const endDate = this.formatDate(this.rangeForm.get('endDate')?.value, true);
      
      this.agentService.getIncidentsByRange(startDate, endDate).subscribe(incidents => {
        this.incidentsList = incidents;
        this.rangeForm.reset();
      } );
    }
  }

  formatDate(date: string, endOfDay: boolean = false): string {
    return endOfDay ? `${date}T23:59:59` : `${date}T00:00:00`;
  }

}
