import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShoppingComponent } from './shopping.component';

describe('ShoppingComponent', () => {
  let component: ShoppingComponent;
  let fixture: ComponentFixture<ShoppingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShoppingComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ShoppingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debería crearse', () => {
    expect(component).toBeTruthy();
  });

  it('debería cargar los artículos del carrito desde sessionStorage', () => {
    const storedItems = JSON.stringify([{ producto: { id: 1, precio: 100 } }]);
    sessionStorage.setItem('compraRealizada', storedItems);
    component.loadCartItems();
    expect(component.cartItems.length).toBe(1);
    expect(component.cartItems[0].producto.id).toBe(1);
  });

  it('debería eliminar un artículo del carrito', () => {
    component.cartItems = [{ producto: { id: 1, precio: 100 } }];
    component.removeFromCart({ producto: { id: 1 } });
    expect(component.cartItems.length).toBe(0);
  });

  it('debería calcular el precio total de los artículos del carrito', () => {
    component.cartItems = [
      { producto: { id: 1, precio: 100 } },
      { producto: { id: 2, precio: 200 } }
    ];
    expect(component.getTotal()).toBe(300);
  });

  it('debería realizar el pago y vaciar el carrito', () => {
    spyOn(window, 'alert');
    component.cartItems = [{ producto: { id: 1, precio: 100 } }];
    component.checkout();
    expect(window.alert).toHaveBeenCalledWith('Pago realizado con éxito');
    expect(component.cartItems.length).toBe(0);
  });

  it('debería guardar los artículos del carrito en el historial', () => {
    const existingHistory = JSON.stringify([{ producto: { id: 2, precio: 200 } }]);
    sessionStorage.setItem('historial', existingHistory);
    component.cartItems = [{ producto: { id: 1, precio: 100 } }];
    component.saveToHistory();
    const history = JSON.parse(sessionStorage.getItem('historial') || '[]');
    expect(history.length).toBe(2);
    expect(history[0].producto.id).toBe(2);
    expect(history[1].producto.id).toBe(1);
  });

  it('debería actualizar el almacenamiento del carrito en sessionStorage', () => {
    component.cartItems = [{ producto: { id: 1, precio: 100 } }];
    component.updateCartStorage();
    const storedItems = JSON.parse(sessionStorage.getItem('compraRealizada') || '[]');
    expect(storedItems.length).toBe(1);
    expect(storedItems[0].producto.id).toBe(1);
  });
});
