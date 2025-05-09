import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditAvatarModalComponent } from './edit-avatar-modal.component';

describe('EditAvatarModalComponent', () => {
  let component: EditAvatarModalComponent;
  let fixture: ComponentFixture<EditAvatarModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditAvatarModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditAvatarModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
