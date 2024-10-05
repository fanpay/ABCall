import { Injectable } from '@angular/core';
import { Incident } from './incident';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor() { }

  getIncidents(): Incident[] {
    return [
      {
        subject: 'Subject 1',
        description: 'Description 1',
        status: 'Status 1',
        originType: 'Origin Type 1',
        solution: 'Solution 1'
      },
      {
        subject: 'Subject 2',
        description: 'Description 2',
        status: 'Status 2',
        originType: 'Origin Type 2',
        solution: 'Solution 2'
      },
      {
        subject: 'Subject 3',
        description: 'Description 3',
        status: 'Status 3',
        originType: 'Origin Type 3',
        solution: 'Solution 3'
      }
    ];
  }

}
