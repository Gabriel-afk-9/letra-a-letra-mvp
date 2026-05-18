import { store } from "../state/store.js";

export class NotificationComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <div id="toast-notification" class="toast hidden">
                <span id="toast-message"></span>
            </div>
        `;
        this.toastEl = this.querySelector('#toast-notification');
        this.messageEl = this.querySelector('#toast-message');

        store.subscribe('notification', (data) => this.show(data));
    }

    show(data) {
        if (!data || !data.message) return;

        this.messageEl.innerText = data.message;
        this.toastEl.className = `toast show ${data.type}`;

        setTimeout(() => {
            this.toastEl.className = 'toast hidden';
        }, 3000);
    }
}
customElements.define("notification-component", NotificationComponent);