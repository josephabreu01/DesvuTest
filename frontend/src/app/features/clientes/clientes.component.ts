import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule, FormBuilder, Validators } from '@angular/forms';
import { Cliente } from '../../core/models/cliente.model';
import { ClienteService } from '../../core/services/cliente.service';
import { NotificationService } from '../../core/services/notification.service';
import { TableComponent, TableColumn } from '../../shared/components/table/table.component';
import { ModalComponent, ModalConfig } from '../../shared/components/modal/modal.component';

@Component({
    selector: 'app-clientes',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, FormsModule, TableComponent, ModalComponent],
    templateUrl: './clientes.component.html',
    styleUrl: './clientes.component.css'
})
export class ClientesComponent implements OnInit {
    private fb = inject(FormBuilder);
    private clienteService = inject(ClienteService);
    private notificationService = inject(NotificationService);

    clienteForm = this.fb.nonNullable.group({
        nombre: ['', [Validators.required]],
        apellido: ['', [Validators.required]],
        direccion: ['', [Validators.required]],
        telefono: ['', [Validators.required]],
        clienteId: [''],
        contrasena: ['', [Validators.required]],
        estado: [true]
    });

    clientes: Cliente[] = [];
    filteredClientes: Cliente[] = [];
    searchTerm = '';
    showModal = false;
    isEditing = false;
    loading = false;
    modalConfig: ModalConfig = {
        title: 'Nuevo Cliente',
        primaryButtonLabel: 'Crear',
        secondaryButtonLabel: 'Cancelar',
        showFooter: true
    };
    selectedId: number | null = null;

    tableColumns: TableColumn[] = [
        { key: 'clienteId', label: 'ClienteId' },
        { key: 'nombre', label: 'Nombres', template: 'name' }, // Custom template for full name
        { key: 'direccion', label: 'Dirección' },
        { key: 'telefono', label: 'Teléfono' },
        { key: 'estado', label: 'Estado', template: 'status' },
        { key: 'acciones', label: 'Acciones', template: 'actions' }
    ];

    notification$ = this.notificationService.notification$;

    ngOnInit(): void {
        this.loadClientes();
    }

    loadClientes(): void {
        this.loading = true;
        this.clienteService.getAll().subscribe({
            next: (data) => {
                this.clientes = data;
                this.filteredClientes = data;
                this.loading = false;
            },
            error: (err) => {
                this.notificationService.showError(err.error?.message || 'Error al cargar clientes');
                this.loading = false;
            }
        });
    }

    search(): void {
        const term = this.searchTerm.toLowerCase().trim();
        this.filteredClientes = term
            ? this.clientes.filter(c =>
                c.nombre.toLowerCase().includes(term) ||
                c.apellido.toLowerCase().includes(term) ||
                c.clienteId?.toLowerCase().includes(term))
            : [...this.clientes];
    }

    openCreate(): void {
        this.clienteForm.reset({ estado: true });
        this.clienteForm.get('contrasena')?.setValidators([Validators.required]);
        this.isEditing = false;
        this.selectedId = null;
        this.showModal = true;
    }

    openEdit(cliente: Cliente): void {
        this.clienteForm.patchValue({
            nombre: cliente.nombre,
            apellido: cliente.apellido,
            direccion: cliente.direccion,
            telefono: cliente.telefono,
            contrasena: '',
            estado: cliente.estado
        });
        this.clienteForm.get('contrasena')?.clearValidators();
        this.clienteForm.get('contrasena')?.updateValueAndValidity();
        this.isEditing = true;
        this.selectedId = cliente.id!;
        this.showModal = true;
    }

    closeModal(): void {
        this.showModal = false;
    }

    save(): void {
        if (this.clienteForm.invalid) {
            this.clienteForm.markAllAsTouched();
            return;
        }

        this.loading = true;
        const formData = this.clienteForm.getRawValue();
        const request = this.isEditing
            ? this.clienteService.update(this.selectedId!, formData)
            : this.clienteService.create(formData);

        request.subscribe({
            next: () => {
                this.notificationService.showSuccess(this.isEditing ? 'Cliente actualizado correctamente' : 'Cliente creado correctamente');
                this.showModal = false;
                this.loadClientes();
            },
            error: (err) => {
                this.notificationService.showError(err.error?.message || 'Error al guardar cliente');
                this.loading = false;
            }
        });
    }

    delete(id: number): void {
        if (!confirm('¿Estás seguro que deseas eliminar este cliente?')) return;
        this.clienteService.delete(id).subscribe({
            next: () => {
                this.notificationService.showSuccess('Cliente eliminado correctamente');
                this.loadClientes();
            },
            error: (err) => {
                this.notificationService.showError(err.error?.message || 'Error al eliminar cliente');
            }
        });
    }
}
