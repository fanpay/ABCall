import { Component, OnInit } from '@angular/core';
import { UserService } from '../user.service';
import { AuthService } from '../../login/auth.service';

interface Message {
  text: string;
  isUser: boolean;
  isError?: boolean;
}

@Component({
  selector: 'app-chatbot',
  templateUrl: './chatbot.component.html',
  styleUrls: ['./chatbot.component.css']
})
export class ChatbotComponent implements OnInit {
  messages: Message[] = [];
  userMessage: string = '';

  constructor(private userService: UserService, private authService: AuthService) { }

  ngOnInit(): void {
  }

  sendMessage(): void {
    if (this.userMessage.trim()) {
      this.messages.push({ text: this.userMessage, isUser: true });
      const token = this.authService.getToken();
      const originType = 'web';
      const userId = this.authService.getLoggedUser().id;
      this.userService.sendMessageToChatbot(this.userMessage, originType, userId, token)
        .subscribe({
          next: response => {
            this.messages.push({ text: response.message, isUser: false });
          },
          error: err => {
            this.messages.push({ text: "Tu mensaje no ha podido ser enviado. El servicio de chatbot se encuentra temporalmente fuera de servicio.", isUser: false, isError: true });
          }
        });
      this.userMessage = '';
    }
  }
}
