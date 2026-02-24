import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class FileService {
    downloadBase64Pdf(base64Data: string, fileName: string): void {
        const linkSource = `data:application/pdf;base64,${base64Data}`;
        const downloadLink = document.createElement("a");
        downloadLink.href = linkSource;
        downloadLink.download = fileName;
        downloadLink.click();
    }
}
