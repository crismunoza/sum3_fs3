import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';  
import { user } from '../../../shared/models/Usuario';  

import { AuthService } from './auth.service';
import { HttpErrorResponse } from '@angular/common/http';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;  

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],  
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController); 
  });

  afterEach(() => {
    httpMock.verify();  
  });

  it('debería ser creado', () => {
    expect(service).toBeTruthy();
  });

  it('debería iniciar sesión con credenciales de administrador', () => {
    const adminUser: user = { username: 'admin', role: 'admin' };  

    service.login(adminUser.username, 'Admin123').subscribe(token => {
      expect(token).toBeTruthy();
    });

    const req = httpMock.expectOne('http://localhost:8080/api/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush('fake-jwt-token');
  });

  it('debería obtener el rol del usuario', () => {
    const fakeJwtToken = `${btoa(JSON.stringify({ alg: "HS256", typ: "JWT" }))}.${btoa(JSON.stringify({ role: "admin" }))}.signature`;
  
    spyOn(sessionStorage, 'getItem').and.returnValue(fakeJwtToken);
    spyOn(service, 'getToken').and.returnValue(fakeJwtToken);
  
    const role = service.getUserRole();
    expect(role).toBe('admin');
  });
  

  it('debería devolver null si no hay token', () => {
    spyOn(sessionStorage, 'getItem').and.returnValue(null);

    const token = service.getToken();
    expect(token).toBeNull();
  });

  it('debería manejar el error de login', () => {
    const errorEvent = new ErrorEvent('Network error');
    const expectedUrl = 'http://localhost:8080/api/auth/login';
  
    service.login('user', 'password').subscribe(
      () => fail('debería fallar'),
      (error: HttpErrorResponse) => {
        expect(error).toBeTruthy(); // Verifica que se reciba un error
        expect(error.error).toBe(errorEvent); // El error interno debería ser el mismo ErrorEvent
        expect(error.status).toBe(0); // Código de estado esperado para errores de red
        expect(error.statusText).toBe('Unknown Error'); // Texto de error esperado
        expect(error.url).toBe(expectedUrl); // La URL debería coincidir
      }
    );
  
    const req = httpMock.expectOne(expectedUrl);
    req.error(errorEvent);
  });
  
});
