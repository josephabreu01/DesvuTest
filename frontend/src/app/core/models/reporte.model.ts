export interface ReporteMovimiento {
    fecha: string;
    tipoMovimiento: string;
    valor: number;
    saldo: number;
}

export interface Reporte {
    clienteNombre: string;
    clienteApellido: string;
    clienteId: string;
    numeroCuenta: string;
    tipoCuenta: string;
    saldoInicial: number;
    estadoCuenta: boolean;
    movimientos: ReporteMovimiento[];
}
