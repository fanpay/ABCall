import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-plan',
  templateUrl: './plan.component.html',
  styleUrls: ['./plan.component.css']
})
export class PlanComponent implements OnInit {

  planForm!: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit() {
    this.planForm = this.formBuilder.group({
      plan: [""],
    });
  }

  savePlan(plan: String) {
    console.info("The plan to created: ", plan)
    //this.toastr.success("Confirmation", "Agente creado!")
    this.planForm.reset();
  }

}
