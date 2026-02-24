import { Persona } from './persona.model';

export interface Cliente extends Persona {
    clienteId?: string;
    contrasena?: string;
    estado: boolean;
}

export interface ClienteRequest extends Persona {
    clienteId?: string;
    contrasena: string;
    estado: boolean;
}
