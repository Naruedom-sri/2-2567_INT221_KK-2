package intregatedproject.backend.dtos.saleitems;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SaleItemImageRequest {
    private String fileName;
    private Integer imageViewOrder;
    private String ogFileName;
    // Use conventional camelCase for binder compatibility: imageFile
    private MultipartFile imageFile;
        private ImageState state;

        public enum ImageState {
            CREATE,
            UPDATE,
            REPLACE,
            DELETE,
            KEEP
        }

    //จารเค้าใช้เป็น order เลยกันไว้้
    public void setOrder(Integer order) { this.imageViewOrder = order; }
    public Integer getOrder() { return this.imageViewOrder; }

    // จารใช้ status แทน state กันไว้เหมือนกัน
    public void setStatus(String status) {
        if (status == null) return;
        switch (status.trim().toUpperCase()) {
            case "CREATE" -> this.state = ImageState.CREATE;      // add new image
            case "ONLINE", "REPLACE" -> this.state = ImageState.REPLACE; // replace existing file
            case "UPDATE" -> this.state = ImageState.UPDATE;   // move/reorder only
            case "DELETE" -> this.state = ImageState.DELETE;   // delete
            case "KEEP" -> this.state = ImageState.KEEP;       // keep as-is
            default -> {
                // Fallback: keep existing
                this.state = ImageState.KEEP;
            }
        }
    }
    public String getStatus() { return this.state == null ? null : this.state.name(); }
}
