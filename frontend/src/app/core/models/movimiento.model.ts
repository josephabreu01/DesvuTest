export type TipoMovimiento = 'DEPOSITO' | 'RETIRO';

export interface Movimiento {
    id?: number;
    fecha: string;
    tipoMovimiento: TipoMovimiento;
    valor: number;
    saldo: number;
    cuentaId: number;
    numeroCuenta?: string;
    tipoCuenta?: string;
    estadoCuenta?: boolean;
}

export interface MovimientoRequest {
    tipoMovimiento: TipoMovimiento;
    valor: number;
    cuentaId: number;
}

