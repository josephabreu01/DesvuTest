import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CuentaService } from '../core/services/cuenta.service';
import { Cuenta, CuentaRequest } from '../core/models/cuenta.model';
import { environment } from '../../environments/environment';

describe('CuentaService', () => {
    let service: CuentaService;
    let httpMock: HttpTestingController;

    const baseUrl = `${environment.apiUrl}/cuentas`;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [CuentaService]
        });
        service = TestBed.inject(CuentaService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should retrieve all cuentas', () => {
        const dummyCuentas: Cuenta[] = [
            { id: 1, numeroCuenta: '123', tipoCuenta: 'Ahorro', saldoInicial: 100, estado: true, clienteId: 1 }
        ];

        service.getAll().subscribe(cuentas => {
            expect(cuentas.length).toBe(1);
            expect(cuentas).toEqual(dummyCuentas);
        });

        const req = httpMock.expectOne(baseUrl);
        expect(req.request.method).toBe('GET');
        req.flush(dummyCuentas);
    });

    it('should retrieve a cuenta by id', () => {
        const dummyCuenta: Cuenta = { id: 1, numeroCuenta: '123', tipoCuenta: 'Ahorro', saldoInicial: 100, estado: true, clienteId: 1 };

        service.getById(1).subscribe(cuenta => {
            expect(cuenta).toEqual(dummyCuenta);
        });

        const req = httpMock.expectOne(`${baseUrl}/1`);
        expect(req.request.method).toBe('GET');
        req.flush(dummyCuenta);
    });

    it('should retrieve cuentas by clienteId', () => {
        const dummyCuentas: Cuenta[] = [{ id: 1, numeroCuenta: '123', tipoCuenta: 'Ahorro', saldoInicial: 100, estado: true, clienteId: 1 }];

        service.getByClienteId(1).subscribe(cuentas => {
            expect(cuentas).toEqual(dummyCuentas);
        });

        const req = httpMock.expectOne(`${baseUrl}/cliente/1`);
        expect(req.request.method).toBe('GET');
        req.flush(dummyCuentas);
    });

    it('should create a new cuenta', () => {
        const requestDTO: CuentaRequest = { tipoCuenta: 'Ahorro', saldoInicial: 100, estado: true, clienteId: 1 };
        const dummyCuenta: Cuenta = { ...requestDTO, id: 1, numeroCuenta: '123' };

        service.create(requestDTO).subscribe(cuenta => {
            expect(cuenta).toEqual(dummyCuenta);
        });

        const req = httpMock.expectOne(baseUrl);
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(requestDTO);
        req.flush(dummyCuenta);
    });

    it('should delete a cuenta', () => {
        service.delete(1).subscribe(res => {
            expect(res).toBeNull();
        });

        const req = httpMock.expectOne(`${baseUrl}/1`);
        expect(req.request.method).toBe('DELETE');
        req.flush(null);
    });
});
