import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ReporteService } from '../core/services/reporte.service';
import { Reporte } from '../core/models/reporte.model';
import { environment } from '../../environments/environment';

describe('ReporteService', () => {
    let service: ReporteService;
    let httpMock: HttpTestingController;

    const baseUrl = `${environment.apiUrl}/reportes`;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [ReporteService]
        });
        service = TestBed.inject(ReporteService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should call getReporte and return reporte data', () => {
        const dummyReporte: Reporte[] = [{
            clienteNombre: 'Juan', clienteApellido: 'Perez', clienteId: 'c1', numeroCuenta: '123', tipoCuenta: 'Ahorro',
            saldoInicial: 100, estadoCuenta: true, movimientos: []
        }];

        service.getReporte('c1', '', '2023-01-01', '2023-12-31').subscribe(reportes => {
            expect(reportes.length).toBe(1);
            expect(reportes).toEqual(dummyReporte);
        });

        const req = httpMock.expectOne(`${baseUrl}?clienteId=c1&nombreCliente=&fechaInicio=2023-01-01&fechaFin=2023-12-31`);
        expect(req.request.method).toBe('GET');
        req.flush(dummyReporte);
    });
});
