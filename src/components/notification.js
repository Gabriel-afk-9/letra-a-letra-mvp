import { store } from "../state/store.js";

export class NotificationComponent extends HTMLElement {
    constructor() {
        super();
        this.timeoutId = null;
        this.unsubscribe = null;
    }

    connectedCallback() {
        this.innerHTML = `
            <div id="toast-notification" class="toast hidden">
                <span id="toast-message"></span>
            </div>
        `;
        
        this.toastEl = this.querySelector('#toast-notification');
        this.messageEl = this.querySelector('#toast-message');
        this.unsubscribe = store.subscribe('notification', (data) => this.show(data));
    }

    disconnectedCallback() {
        if (this.timeoutId) {
            clearTimeout(this.timeoutId);
        }
        if (this.unsubscribe) {
            this.unsubscribe();
        }
    }

    show(data) {
        if (!data || !data.message) return;

        if (this.timeoutId) {
            clearTimeout(this.timeoutId);
        }

        this.messageEl.innerText = data.message;
        this.toastEl.className = `toast show ${data.type || ''}`;

        this.timeoutId = setTimeout(() => {
            this.toastEl.className = 'toast hidden';
        }, 3000);
    }
}
customElements.define("notification-component", NotificationComponent);