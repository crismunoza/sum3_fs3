import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { AuthService } from '../../core/service/auth/auth.service';
import Swal from 'sweetalert2';
import { of, throwError } from 'rxjs';

import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, LoginComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show error if username or password is empty', () => {
    spyOn(Swal, 'fire');
    component.username = '';
    component.password = '';
    component.onLogin();
    expect(Swal.fire).toHaveBeenCalledWith('Error', 'Por favor, ingresa tu usuario y contraseña', 'error');
  });

  it('should call authService.login and navigate on success', () => {
    spyOn(authService, 'login').and.returnValue(of('fake-token'));
    spyOn(Swal, 'fire').and.returnValue(Promise.resolve({} as any));
    spyOn(router, 'navigate');

    component.username = 'testuser';
    component.password = 'testpassword';
    component.onLogin();

    expect(authService.login).toHaveBeenCalledWith('testuser', 'testpassword');
    expect(Swal.fire).toHaveBeenCalled();
    Swal.fire().then(() => {
      expect(router.navigate).toHaveBeenCalledWith(['/']);
    });
  });

  it('should show error if login fails', () => {
    spyOn(authService, 'login').and.returnValue(throwError({ error: 'Usuario o contraseña incorrectos' }));
    spyOn(Swal, 'fire');

    component.username = 'testuser';
    component.password = 'testpassword';
    component.onLogin();

    expect(authService.login).toHaveBeenCalledWith('testuser', 'testpassword');
    expect(Swal.fire).toHaveBeenCalledWith(jasmine.objectContaining({
      title: 'Error',
      text: 'Usuario o contraseña incorrectos',
      icon: 'error',
      timer: 3000,
      showConfirmButton: false,
    }));
  });
});
