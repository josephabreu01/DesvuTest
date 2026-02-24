import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { MovimientoService } from '../core/services/movimiento.service';
import { Movimiento, MovimientoRequest } from '../core/models/movimiento.model';
import { environment } from '../../environments/environment';

describe('MovimientoService', () => {
    let service: MovimientoService;
    let httpMock: HttpTestingController;

    const baseUrl = `${environment.apiUrl}/movimientos`;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [MovimientoService]
        });
        service = TestBed.inject(MovimientoService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should retrieve all movimientos', () => {
        const dummyMovimientos: Movimiento[] = [
            { id: 1, fecha: '2023-01-01', tipoMovimiento: 'DEPOSITO', valor: 100, saldo: 100, cuentaId: 1 }
        ];

        service.getAll().subscribe(movimientos => {
            expect(movimientos.length).toBe(1);
            expect(movimientos).toEqual(dummyMovimientos);
        });

        const req = httpMock.expectOne(baseUrl);
        expect(req.request.method).toBe('GET');
        req.flush(dummyMovimientos);
    });

    it('should retrieve a movimiento by id', () => {
        const dummyMov: Movimiento = { id: 1, fecha: '2023-01-01', tipoMovimiento: 'DEPOSITO', valor: 100, saldo: 100, cuentaId: 1 };

        service.getById(1).subscribe(movimiento => {
            expect(movimiento).toEqual(dummyMov);
        });

        const req = httpMock.expectOne(`${baseUrl}/1`);
        expect(req.request.method).toBe('GET');
        req.flush(dummyMov);
    });

    it('should create a movimiento', () => {
        const requestDTO: MovimientoRequest = { tipoMovimiento: 'DEPOSITO', valor: 100, cuentaId: 1 };
        const dummyMov: Movimiento = { ...requestDTO, id: 1, fecha: '2023-01-01', saldo: 100 };

        service.create(requestDTO).subscribe(movimiento => {
            expect(movimiento).toEqual(dummyMov);
        });

        const req = httpMock.expectOne(baseUrl);
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(requestDTO);
        req.flush(dummyMov);
    });
});
