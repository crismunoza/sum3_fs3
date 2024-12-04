import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { UserService } from '../../core/service/user/user.service';
import Swal from 'sweetalert2';
import { of, throwError } from 'rxjs';


import { AdminComponent } from './admin.component';

describe('AdminComponent', () => {
  let component: AdminComponent;
  let fixture: ComponentFixture<AdminComponent>;
  let userService: UserService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, AdminComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    userService = TestBed.inject(UserService);
  });

  it('debería crear el componente', () => {
    expect(component).toBeTruthy();
  });

  it('debería eliminar un usuario', async () => {
    spyOn(userService, 'deleteUsuario').and.returnValue(of([]));
  
    // Mockear Swal.fire para que retorne un objeto con todas las propiedades esperadas
    spyOn(Swal, 'fire').and.returnValue(Promise.resolve({
      isConfirmed: true,
      isDenied: false,
      isDismissed: false
    }));
  
    component.usuarios = [{ id: 1, name: 'Test User' }];
    
    // Llamar a eliminarUsuario y esperar la resolución de la promesa
    await component.eliminarUsuario(1);
  
    // Verificar que el servicio fue llamado y la lista de usuarios fue actualizada
    expect(userService.deleteUsuario).toHaveBeenCalledWith(1);
    expect(component.usuarios.length).toBe(0);
  });

  
  it('debería actualizar un usuario', () => {
    const usuario = { id: 1, name: 'Test User', editing: true };
    spyOn(userService, 'editUsuario').and.returnValue(of(usuario));
    spyOn(Swal, 'fire');

    component.guardarUsuario(usuario);

    expect(userService.editUsuario).toHaveBeenCalledWith(usuario);
    expect(usuario.editing).toBe(false);
    expect(Swal.fire).toHaveBeenCalledWith('Actualizado', 'El usuario ha sido actualizado', 'success');
  });

  it('debería manejar el error al actualizar un usuario', () => {
    const usuario = { id: 1, name: 'Test User', editing: true };
    spyOn(userService, 'editUsuario').and.returnValue(throwError('Error'));
    spyOn(console, 'error');
    spyOn(Swal, 'fire');

    component.guardarUsuario(usuario);

    expect(userService.editUsuario).toHaveBeenCalledWith(usuario);
    expect(console.error).toHaveBeenCalledWith('Error al actualizar usuario:', 'Error');
    expect(Swal.fire).toHaveBeenCalledWith('Error', 'No se pudo actualizar el usuario', 'error');
  });

  it('debería cancelar la edición del usuario', () => {
    const usuario = { id: 1, name: 'Test User', editing: true };

    component.cancelarEdicion(usuario);

    expect(usuario.editing).toBe(false);
  });
});
