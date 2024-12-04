import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { AddproductComponent } from './addproduct.component';
import Swal from 'sweetalert2';

describe('AddproductComponent', () => {
  let component: AddproductComponent;
  let fixture: ComponentFixture<AddproductComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, AddproductComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddproductComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('debería inicializarse con valores predeterminados', () => {
    expect(component.imageName).toBe('');
    expect(component.imagePreview).toBeNull();
    expect(component.producto.nombre).toBe('');
    expect(component.producto.descripcion).toBe('');
    expect(component.producto.precio).toBe(0);
    expect(component.producto.stock).toBe(0);
    expect(component.producto.nuevo).toBe(1);
    expect(component.imagen).toBeNull();
  });

  it('debería establecer imageName y imagePreview al seleccionar un archivo', () => {
    const file = new File([''], 'test-image.png', { type: 'image/png' });
    const event = { target: { files: [file] } } as unknown as Event;

    component.onFileSelect(event);

    expect(component.imageName).toBe('test-image.png');
    expect(component.imagen).toBe(file);
  });

  it('debería mostrar error si faltan campos obligatorios', () => {
    spyOn(window, 'alert');
    component.producto.nombre = '';
    component.producto.descripcion = '';
    component.crearProducto();
    expect(Swal.isVisible()).toBeTrue();
  });

  it('debería mostrar error si el precio es menor o igual a cero', () => {
    spyOn(window, 'alert');
    component.producto.precio = 0;
    component.crearProducto();
    expect(Swal.isVisible()).toBeTrue();
  });

  it('debería mostrar error si el stock es menor o igual a cero', () => {
    spyOn(window, 'alert');
    component.producto.stock = 0;
    component.crearProducto();
    expect(Swal.isVisible()).toBeTrue();
  });

  it('debería mostrar error si no se selecciona una imagen', () => {
    spyOn(window, 'alert');
    component.imagen = null;
    component.crearProducto();
    expect(Swal.isVisible()).toBeTrue();
  });

  it('debería llamar a productoService.registrarProducto al enviar el formulario válido', () => {
    const productoServiceSpy = spyOn(component['productoService'], 'registrarProducto').and.callThrough();
    component.producto.nombre = 'Test Product';
    component.producto.descripcion = 'Test Description';
    component.producto.precio = 100;
    component.producto.stock = 10;
    component.imagen = new File([''], 'test-image.png', { type: 'image/png' });

    component.crearProducto();

    expect(productoServiceSpy).toHaveBeenCalled();
  });
});
