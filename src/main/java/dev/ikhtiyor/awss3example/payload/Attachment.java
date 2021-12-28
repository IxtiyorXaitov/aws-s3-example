package dev.ikhtiyor.awss3example.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {

    private byte[] bytes;

    private String contentType;
}
