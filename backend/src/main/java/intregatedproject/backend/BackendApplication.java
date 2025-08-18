package intregatedproject.backend;


import intregatedproject.backend.dtos.saleitems.RequestSaleItemDto;
import intregatedproject.backend.entities.SaleItem;
import intregatedproject.backend.utils.FileStorageProperties;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
@EnableConfigurationProperties(FileStorageProperties.class)
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {

        return new ModelMapper();
    }
//    @Bean
//    public ModelMapper modelMapper() {
//        ModelMapper mapper = new ModelMapper();
//
//        // ข้าม field id เวลา map จาก DTO ไป entity
//        mapper.typeMap(RequestSaleItemDto.class, SaleItem.class)
//                .addMappings(m -> m.skip(SaleItem::setId));
//
//        return mapper;
//    }
}
