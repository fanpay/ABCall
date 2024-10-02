export class Incident {
    subject: string;
    description: string;
    status: string;
    originType: string;
    solution: string;
    
    constructor(subject: string, description: string, status: string, originType: string, solution: string) {
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.originType = originType;
        this.solution = solution;
    }
    
}
