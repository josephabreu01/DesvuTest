import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
    return next(req).pipe(
        catchError((error: HttpErrorResponse) => {
            let errorMessage = 'Ha ocurrido un error inesperado';

            if (error.error instanceof ErrorEvent) {
                // Error de lado del cliente
                errorMessage = `Error: ${error.error.message}`;
            } else {
                // Error de lado del servidor
                if (error.status === 0) {
                    errorMessage = 'No se pudo conectar con el servidor. Verifique si el backend está en ejecución.';
                } else if (error.error && error.error.message) {
                    errorMessage = error.error.message;
                } else {
                    errorMessage = `Código de error del servidor: ${error.status}\nMensaje: ${error.message}`;
                }
            }

            console.error('HTTP Error Interceptor:', errorMessage);

            // Retornamos un objeto con la estructura que esperan los componentes: { error: { message: ... } }
            return throwError(() => ({ error: { message: errorMessage } }));
        })
    );
};
