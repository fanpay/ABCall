import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-incident-create',
  templateUrl: './incident-create.component.html',
  styleUrls: ['./incident-create.component.css']
})
export class IncidentCreateComponent implements OnInit {



  incidentForm !: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private toastrService: ToastrService
  ) { }

  ngOnInit() {
    this.incidentForm = this.formBuilder.group({
      subject: ["", Validators.required],
      description: ["", Validators.required],
     });
  }

  createIncident(incident: any) {
    console.log(incident);
    this.toastrService.success("La incidencia fue creada", "Confirmacion")
    this.incidentForm.reset();
  }

  cancelCreation() {
    this.incidentForm.reset();
  }

}
