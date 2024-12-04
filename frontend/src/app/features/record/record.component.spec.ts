import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecordComponent } from './record.component';

describe('RecordComponent', () => {
  let component: RecordComponent;
  let fixture: ComponentFixture<RecordComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecordComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RecordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debería crearse', () => {
    expect(component).toBeTruthy();
  });

  it('debería cargar los artículos del historial desde sessionStorage', () => {
    const mockHistory = JSON.stringify([{ id: 1, name: 'Test Item' }]);
    spyOn(sessionStorage, 'getItem').and.returnValue(mockHistory);

    component.loadHistoryItems();

    expect(component.historyItems).toEqual(JSON.parse(mockHistory));
  });

  it('debería manejar un historial vacío en sessionStorage', () => {
    sessionStorage.setItem('historial', JSON.stringify([]));
    component.loadHistoryItems();
    expect(component.historyItems.length).toBe(0);
  });
});
