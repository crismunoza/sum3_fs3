import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';
import Swal from 'sweetalert2';
import { RegisterComponent } from './register.component';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RegisterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('debería crear el componente', () => {
    expect(component).toBeTruthy();
  });

  it('debería mostrar una alerta si los campos están vacíos', () => {
    spyOn(Swal, 'fire');
    component.username = '';
    component.password = '';
    component.email = '';
    component.nombre = '';
    component.apellido = '';
  
    component.registerUsuario();
  
    expect(Swal.fire).toHaveBeenCalledWith(
      'Validación', 
      'Por favor, completa todos los campos correctamente.', 
      'warning'
    );
  });
  
  it('debería registrar un usuario correctamente', () => {
    spyOn(component['userService'], 'registerUsuario').and.returnValue(of({}));
    spyOn(Swal, 'fire');
    spyOn(component['router'], 'navigate');

    component.username = 'usuario';
    component.password = 'password';
    component.email = 'email@example.com';
    component.nombre = 'Nombre';
    component.apellido = 'Apellido';
    component.registerUsuario();

    expect(component['userService'].registerUsuario).toHaveBeenCalled();
    expect(Swal.fire).toHaveBeenCalledWith('Usuario registrado', 'El usuario ha sido registrado correctamente', 'success');
    expect(component['router'].navigate).toHaveBeenCalledWith(['/Login']);
  });

  it('debería mostrar un error si el registro falla', () => {
    spyOn(component['userService'], 'registerUsuario').and.returnValue(throwError({}));
    spyOn(Swal, 'fire');

    component.username = 'usuario';
    component.password = 'password';
    component.email = 'email@example.com';
    component.nombre = 'Nombre';
    component.apellido = 'Apellido';
    component.registerUsuario();

    expect(component['userService'].registerUsuario).toHaveBeenCalled();
    expect(Swal.fire).toHaveBeenCalledWith('Error', 'Ha ocurrido un error al registrar el usuario', 'error');
  });
});
