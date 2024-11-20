import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { PlanService } from './plan.service';
import { Plan } from './plan';

@Component({
  selector: 'app-plan',
  templateUrl: './plan.component.html',
  styleUrls: ['./plan.component.css']
})
export class PlanComponent implements OnInit {

  planForm!: FormGroup;
  actualPlan: Plan = new Plan(0, "Default Plan");

  constructor(
    private formBuilder: FormBuilder,
    private toastrService: ToastrService,
    private planService: PlanService
  ) { }

  ngOnInit() {
    this.getActualPlan();
    this.planForm = this.formBuilder.group({
      plan: [""],
    });
  }

  getActualPlan() {
    this.planService.getActualPlan().subscribe(res => {
      console.info("The actual plan is: ", res)
      this.actualPlan = res;
    });
  }

  savePlan(plan: Plan) {
    console.info("The plan to created: ", plan)
    this.planService.updatePlan(plan).subscribe(res => {
      this.toastrService.success("Confirmation", "El plan fue actualizado!")
      this.planForm.reset();
      this.actualPlan = res;
      console.info("The plan was updated: ", res)
    });
  }

}
