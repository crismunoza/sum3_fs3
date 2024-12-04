import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';

@Component({
  selector: 'app-shopping',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './shopping.component.html',
  styleUrl: './shopping.component.css'
})
export class ShoppingComponent implements OnInit {
  cartItems: any[] = [];

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {}

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      this.loadCartItems();
    }
  }

  loadCartItems() {
    const storedItems = sessionStorage.getItem('compraRealizada');
    if (storedItems) {
      this.cartItems = JSON.parse(storedItems);
    }
  }

  removeFromCart(item: any) {
    this.cartItems = this.cartItems.filter(cartItem => cartItem.producto.id !== item.producto.id);
    this.updateCartStorage();
  }

  getTotal() {
    return this.cartItems.reduce((total, item) => total + item.producto.precio, 0);
  }

  checkout() {
    // Lógica para el pago
    alert('Pago realizado con éxito');
    this.saveToHistory();
    this.cartItems = [];
    this.updateCartStorage();
  }

  saveToHistory() {
    const history = sessionStorage.getItem('historial');
    let historyItems = history ? JSON.parse(history) : [];
    historyItems = historyItems.concat(this.cartItems);
    sessionStorage.setItem('historial', JSON.stringify(historyItems));
  }

  updateCartStorage() {
    sessionStorage.setItem('compraRealizada', JSON.stringify(this.cartItems));
  }
}
