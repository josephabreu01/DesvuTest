import { Component, Input, TemplateRef } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface TableColumn {
    key: string;
    label: string;
    template?: string; // Optional identifier for custom templates
}

@Component({
    selector: 'app-table',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './table.component.html',
    styleUrl: './table.component.css'
})
export class TableComponent {
    @Input() columns: TableColumn[] = [];
    @Input() data: any[] = [];
    @Input() loading = false;
    @Input() emptyMessage = 'No se encontraron datos';
    @Input() emptyIcon = '📂';

    // To allow custom cell templates via injection
    @Input() customTemplates: { [key: string]: TemplateRef<any> } = {};

    getColumnValue(item: any, key: string): any {
        if (!key.includes('.')) return item[key];
        return key.split('.').reduce((obj, k) => obj && obj[k], item);
    }
}
