export interface Cuenta {
    id?: number;
    numeroCuenta: string;
    tipoCuenta: string;
    saldoInicial: number;
    estado: boolean;
    clienteId: number;
    clienteNombre?: string;
    clienteIdentificacion?: string;
}

export interface CuentaRequest {
    tipoCuenta: string;
    saldoInicial: number;
    estado: boolean;
    clienteId: number;
}
