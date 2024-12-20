import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';

import { HeadersComponent } from './headers.component';

describe('HeadersComponent', () => {
  let component: HeadersComponent;
  let fixture: ComponentFixture<HeadersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeadersComponent, HttpClientModule],
      providers: [{ provide: ActivatedRoute, useValue: {} }]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HeadersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('debería verificar el estado de inicio de sesión', () => {
    spyOn(component, 'checkLoginStatus').and.callThrough();
    component.ngOnInit();
    expect(component.checkLoginStatus).toHaveBeenCalled();
  });

  it('debería cerrar sesión correctamente', () => {
    spyOn(component.authService, 'logout').and.callThrough();
    component.logout();
    expect(component.authService.logout).toHaveBeenCalled();
  });
});
