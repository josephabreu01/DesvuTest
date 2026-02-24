import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ClienteService } from '../core/services/cliente.service';
import { Cliente, ClienteRequest } from '../core/models/cliente.model';
import { environment } from '../../environments/environment.prod';

describe('ClienteService', () => {
    let service: ClienteService;
    let httpMock: HttpTestingController;

    const baseUrl = `${environment.apiUrl}/clientes`;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [ClienteService]
        });
        service = TestBed.inject(ClienteService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('getAll() should return a list of clientes', () => {
        const mockClientes: Cliente[] = [
            { id: 1, nombre: 'Juan', apellido: 'Perez', direccion: 'Calle 1', telefono: '0999', clienteId: 'juan01', estado: true }
        ];

        service.getAll().subscribe(clientes => {
            expect(clientes.length).toBe(1);
            expect(clientes[0].nombre).toBe('Juan');
            expect(clientes[0].apellido).toBe('Perez');
            expect(clientes[0].clienteId).toBe('juan01');
        });

        const req = httpMock.expectOne(baseUrl);
        expect(req.request.method).toBe('GET');
        req.flush(mockClientes);
    });

    it('create() should POST and return the created cliente', () => {
        const request: ClienteRequest = {
            nombre: 'Maria',
            apellido: 'Lopez',
            direccion: 'Calle 2', telefono: '0988', clienteId: 'maria01', contrasena: 'pass123', estado: true
        };
        const mockResponse: Cliente = { ...request, id: 2 };

        service.create(request).subscribe(result => {
            expect(result.id).toBe(2);
            expect(result.nombre).toBe('Maria');
            expect(result.apellido).toBe('Lopez');
        });

        const req = httpMock.expectOne(baseUrl);
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(request);
        req.flush(mockResponse);
    });

    it('delete() should send DELETE request', () => {
        service.delete(1).subscribe();

        const req = httpMock.expectOne(`${baseUrl}/1`);
        expect(req.request.method).toBe('DELETE');
        req.flush(null);
    });

    it('getById() should return a single cliente', () => {
        const mockCliente: Cliente = {
            id: 1, nombre: 'Juan', apellido: 'Perez',
            direccion: 'Calle 1', telefono: '0999', clienteId: 'juan01', estado: true
        };

        service.getById(1).subscribe(result => {
            expect(result.id).toBe(1);
            expect(result.clienteId).toBe('juan01');
        });

        const req = httpMock.expectOne(`${baseUrl}/1`);
        expect(req.request.method).toBe('GET');
        req.flush(mockCliente);
    });
});
