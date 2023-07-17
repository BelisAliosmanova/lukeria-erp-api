package com.example.ludogoriesoft.lukeriaerpapi.services;

import com.example.ludogoriesoft.lukeriaerpapi.dtos.PackageDTO;
import com.example.ludogoriesoft.lukeriaerpapi.models.Carton;
import com.example.ludogoriesoft.lukeriaerpapi.models.Package;
import com.example.ludogoriesoft.lukeriaerpapi.models.Plate;
import com.example.ludogoriesoft.lukeriaerpapi.repository.CartonRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PackageRepository;
import com.example.ludogoriesoft.lukeriaerpapi.repository.PlateRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PackageServiceTest {
    @Mock
    private PackageRepository packageRepository;
    @Mock
    private CartonRepository cartonRepository;
    @Mock
    private PlateRepository plateRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PackageService packageService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllPackages() {
        Package package1 = new Package();
        package1.setId(1L);
        package1.setName("Package 1");

        Package package2 = new Package();
        package2.setId(2L);
        package2.setName("Package 2");

        List<Package> mockPackages = Arrays.asList(package1, package2);
        when(packageRepository.findByDeletedFalse()).thenReturn(mockPackages);

        PackageDTO packageDTO1 = new PackageDTO();
        packageDTO1.setId(1L);
        packageDTO1.setName("Package 1");

        PackageDTO packageDTO2 = new PackageDTO();
        packageDTO2.setId(2L);
        packageDTO2.setName("Package 2");

        when(modelMapper.map(package1, PackageDTO.class)).thenReturn(packageDTO1);
        when(modelMapper.map(package2, PackageDTO.class)).thenReturn(packageDTO2);

        List<PackageDTO> result = packageService.getAllPackages();

        assertEquals(mockPackages.size(), result.size());
        assertEquals(mockPackages.get(0).getName(), result.get(0).getName());
        assertEquals(mockPackages.get(1).getName(), result.get(1).getName());

        verify(packageRepository, times(1)).findByDeletedFalse();

        verify(modelMapper, times(mockPackages.size())).map(any(Package.class), eq(PackageDTO.class));
    }


    @Test
    void testCreatePackageWithPlateIdNotNullAndPlateDoesNotExist() {
        // Arrange
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("Test Package");
        packageDTO.setPiecesCarton(10);
        packageDTO.setAvailableQuantity(20);
        packageDTO.setPrice(9.99);
        packageDTO.setCartonId(1L);
        packageDTO.setPlateId(1L);

        when(plateRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        Assertions.assertThrows(ValidationException.class, () -> packageService.createPackage(packageDTO));
    }

    @Test
    void testUpdatePackage_ReturnsUpdatedPackageDTO() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Long packageId = 1L;
        Package existingPackage = new Package();
        existingPackage.setId(packageId);

        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setId(packageId);
        packageDTO.setName("Updated Package");
        packageDTO.setPiecesCarton(10);
        packageDTO.setAvailableQuantity(20);
        packageDTO.setPrice(9.99);
        packageDTO.setCartonId(1L);
        packageDTO.setPlateId(1L);

        when(packageRepository.findByIdAndDeletedFalse(packageId)).thenReturn(java.util.Optional.of(existingPackage));
        when(cartonRepository.existsById(packageDTO.getCartonId())).thenReturn(true);
        when(plateRepository.existsById(packageDTO.getPlateId())).thenReturn(true);
        when(modelMapper.map(packageDTO, Package.class)).thenReturn(existingPackage);
        when(packageRepository.save(existingPackage)).thenReturn(existingPackage);
        when(modelMapper.map(existingPackage, PackageDTO.class)).thenReturn(packageDTO);

        // Act
        PackageDTO updatedPackageDTO = packageService.updatePackage(packageId, packageDTO);

        // Assert
        assertNotNull(updatedPackageDTO);
        assertEquals("Updated Package", updatedPackageDTO.getName());
        // Add more assertions based on your requirements
    }


    @Test
    void testUpdatePackage_ThrowsValidationException_WhenPlateDoesNotExist() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Long packageId = 1L;
        Package existingPackage = new Package();
        existingPackage.setId(packageId);

        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setId(packageId);
        packageDTO.setName("Updated Package");
        packageDTO.setPiecesCarton(10);
        packageDTO.setAvailableQuantity(20);
        packageDTO.setPrice(9.99);
        packageDTO.setCartonId(1L);
        packageDTO.setPlateId(1L);

        when(packageRepository.findByIdAndDeletedFalse(packageId)).thenReturn(java.util.Optional.of(existingPackage));
        when(cartonRepository.existsById(packageDTO.getCartonId())).thenReturn(true);
        when(plateRepository.existsById(packageDTO.getPlateId())).thenReturn(false); // Simulate non-existing plate
        when(modelMapper.map(packageDTO, Package.class)).thenReturn(existingPackage);

        // Act and Assert
        assertThrows(ValidationException.class, () -> packageService.updatePackage(packageId, packageDTO));
    }
    @Test
    void testUpdatePackage_ThrowsValidationException_WhenPlateNull() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Long packageId = 1L;
        Package existingPackage = new Package();
        existingPackage.setId(packageId);

        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setId(packageId);
        packageDTO.setName("Updated Package");
        packageDTO.setPiecesCarton(10);
        packageDTO.setAvailableQuantity(20);
        packageDTO.setPrice(9.99);
        packageDTO.setCartonId(1L);
        packageDTO.setPlateId(null);

        when(packageRepository.findByIdAndDeletedFalse(packageId)).thenReturn(java.util.Optional.of(existingPackage));
        when(cartonRepository.existsById(packageDTO.getCartonId())).thenReturn(true);
        when(plateRepository.existsById(packageDTO.getPlateId())).thenReturn(false); // Simulate non-existing plate
        when(modelMapper.map(packageDTO, Package.class)).thenReturn(existingPackage);

        // Act and Assert
        assertThrows(ValidationException.class, () -> packageService.updatePackage(packageId, packageDTO));
    }

    @Test
    void testCreatePackage_ThrowsValidationException_WhenPlateDoesNotExist() {
        // Arrange
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("New Package");
        packageDTO.setPiecesCarton(10);
        packageDTO.setAvailableQuantity(20);
        packageDTO.setPrice(9.99);
        packageDTO.setCartonId(1L);
        packageDTO.setPlateId(1L);

        when(cartonRepository.existsById(packageDTO.getCartonId())).thenReturn(true);
        when(plateRepository.existsById(packageDTO.getPlateId())).thenReturn(false); // Simulate non-existing plate

        // Act and Assert
        assertThrows(ValidationException.class, () -> packageService.createPackage(packageDTO));
    }

    @Test
    void testCreatePackageWithPlateIdNull() {
        // Arrange
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("Test Package");
        packageDTO.setPiecesCarton(10);
        packageDTO.setAvailableQuantity(20);
        packageDTO.setPrice(9.99);
        packageDTO.setCartonId(1L);
        packageDTO.setPlateId(null);

        // Act & Assert
        Assertions.assertThrows(ValidationException.class, () -> packageService.createPackage(packageDTO));
    }

    @Test
    void testGetPackageById_ExistingId() throws ChangeSetPersister.NotFoundException {
        Package package1 = new Package();
        package1.setId(1L);
        package1.setName("Package 1");

        when(packageRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(package1));

        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setId(1L);
        packageDTO.setName("Package 1");

        when(modelMapper.map(package1, PackageDTO.class)).thenReturn(packageDTO);

        PackageDTO result = packageService.getPackageById(1L);

        assertEquals(packageDTO.getId(), result.getId());
        assertEquals(packageDTO.getName(), result.getName());

        verify(packageRepository, times(1)).findByIdAndDeletedFalse(1L);
        verify(modelMapper, times(1)).map(package1, PackageDTO.class);
    }

    @Test
    void testGetPackageById_NonExistingId() {
        when(packageRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> packageService.getPackageById(1L));

        verify(packageRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testCreatePackage_InvalidPackageDTO_NameMissing() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setPiecesCarton(11);
        packageDTO.setAvailableQuantity(10);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(100);
        packageDTO.setCartonId(1L);

        ValidationException exception = assertThrows(ValidationException.class, () -> packageService.createPackage(packageDTO));
        assertEquals("Name is required", exception.getMessage());

        verifyNoInteractions(modelMapper);

        verifyNoInteractions(packageRepository);
    }

    @Test
    void testCreatePackage_InvalidPackageDTO_InvalidPiecesCarton() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("name");
        packageDTO.setPiecesCarton(0);
        packageDTO.setAvailableQuantity(0);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(0);
        packageDTO.setCartonId(1L);

        ValidationException exception = assertThrows(ValidationException.class, () -> packageService.createPackage(packageDTO));
        assertEquals("Pieces of carton must be greater than zero", exception.getMessage());

        verifyNoInteractions(modelMapper);

        verifyNoInteractions(packageRepository);
    }

    @Test
    void testCreatePackage_InvalidPackageDTO_InvalidCartonId() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("name");
        packageDTO.setPiecesCarton(11);
        packageDTO.setAvailableQuantity(10);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(100);
        packageDTO.setCartonId(1L);
        Mockito.when(cartonRepository.existsById(packageDTO.getCartonId())).thenReturn(false);
        ValidationException exception = assertThrows(ValidationException.class, () -> packageService.createPackage(packageDTO));
        assertEquals("Carton does not exist with ID: " + packageDTO.getCartonId(), exception.getMessage());

        verifyNoInteractions(modelMapper);

        verifyNoInteractions(packageRepository);
    }

    @Test
    void testCreatePackage_InvalidPackageDTO_InvalidCartonIdIsNull() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("name");
        packageDTO.setPiecesCarton(11);
        packageDTO.setAvailableQuantity(10);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(100);
        packageDTO.setCartonId(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> packageService.createPackage(packageDTO));
        assertEquals("Carton ID cannot be null!", exception.getMessage());

        verifyNoInteractions(modelMapper);

        verifyNoInteractions(packageRepository);
    }

    @Test
    void testCreatePackage_InvalidPackageDTO_AvailableQuantityIsInvalid() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("name");
        packageDTO.setPiecesCarton(11);
        packageDTO.setAvailableQuantity(0);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(100);
        packageDTO.setCartonId(1L);

        ValidationException exception = assertThrows(ValidationException.class, () -> packageService.createPackage(packageDTO));
        assertEquals("Available quantity be greater than zero", exception.getMessage());

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(packageRepository);
    }

    @Test
    void testCreatePackage_InvalidPackageDTO_PriceIsZero() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("name");
        packageDTO.setPiecesCarton(11);
        packageDTO.setAvailableQuantity(10);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(0);
        packageDTO.setCartonId(1L);

        ValidationException exception = assertThrows(ValidationException.class, () -> packageService.createPackage(packageDTO));
        assertEquals("Price must be greater than zero", exception.getMessage());

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(packageRepository);
    }

    @Test
    void testCreatePackage_InvalidPackageDTO_PriceIsNegative() {
        PackageDTO packageDTO = new PackageDTO();
        Carton carton = new Carton();
        carton.setId(1L);
        packageDTO.setName("name");
        packageDTO.setPiecesCarton(1);
        packageDTO.setAvailableQuantity(1);
        packageDTO.setPrice(0);
        packageDTO.setPhoto("Photo");
        packageDTO.setCartonId(1L);

        ValidationException exception = assertThrows(ValidationException.class, () -> packageService.createPackage(packageDTO));
        assertEquals("Price must be greater than zero", exception.getMessage());

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(packageRepository);
    }

    @Test
    void testUpdatePackage_InvalidName() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setPiecesCarton(11);
        packageDTO.setAvailableQuantity(10);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(100);
        packageDTO.setCartonId(1L);

        Long id = 1L;
        Package existingPackage = new Package();
        existingPackage.setId(id);
        existingPackage.setName("Test Package");
        existingPackage.setPiecesCarton(10);
        existingPackage.setAvailableQuantity(100);
        existingPackage.setPrice(50.0);
        Carton carton = new Carton();
        carton.setId(1L);
        existingPackage.setCartonId(carton);

        when(packageRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingPackage));
        assertThrows(ValidationException.class, () -> packageService.updatePackage(1L, packageDTO));
        verify(packageRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testUpdatePackage_InvalidPiecesCarton() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("name");
        packageDTO.setPiecesCarton(-11);
        packageDTO.setAvailableQuantity(10);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(100);
        packageDTO.setCartonId(1L);

        Long id = 1L;
        Package existingPackage = new Package();
        existingPackage.setId(id);
        existingPackage.setName("Test Package");
        existingPackage.setPiecesCarton(10);
        existingPackage.setAvailableQuantity(100);
        existingPackage.setPrice(50.0);
        Carton carton = new Carton();
        carton.setId(1L);
        existingPackage.setCartonId(carton);

        when(packageRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingPackage));
        assertThrows(ValidationException.class, () -> packageService.updatePackage(1L, packageDTO));
        verify(packageRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testUpdatePackage_CartonIdNull() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("name");
        packageDTO.setPiecesCarton(11);
        packageDTO.setAvailableQuantity(10);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(100);
        packageDTO.setCartonId(null);

        Long id = 1L;
        Package existingPackage = new Package();
        existingPackage.setId(id);
        existingPackage.setName("Test Package");
        existingPackage.setPiecesCarton(10);
        existingPackage.setAvailableQuantity(100);
        existingPackage.setPrice(50.0);
        Carton carton = new Carton();
        carton.setId(1L);
        existingPackage.setCartonId(carton);

        when(packageRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingPackage));
        assertThrows(ValidationException.class, () -> packageService.updatePackage(1L, packageDTO));
        verify(packageRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testUpdatePackage_CartonIdDoesNotExist() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("name");
        packageDTO.setPiecesCarton(11);
        packageDTO.setAvailableQuantity(10);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(100);
        packageDTO.setCartonId(1L);

        Long id = 1L;
        Package existingPackage = new Package();
        existingPackage.setId(id);
        existingPackage.setName("Test Package");
        existingPackage.setPiecesCarton(10);
        existingPackage.setAvailableQuantity(100);
        existingPackage.setPrice(50.0);
        Carton carton = new Carton();
        carton.setId(1L);
        existingPackage.setCartonId(carton);

        when(packageRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingPackage));
        Mockito.when(cartonRepository.existsById(packageDTO.getCartonId())).thenReturn(false);
        assertThrows(ValidationException.class, () -> packageService.updatePackage(1L, packageDTO));
        verify(packageRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testUpdatePackage_InvalidPrice() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("name");
        packageDTO.setPiecesCarton(11);
        packageDTO.setAvailableQuantity(10);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(-100);
        packageDTO.setCartonId(1L);

        Long id = 1L;
        Package existingPackage = new Package();
        existingPackage.setId(id);
        existingPackage.setName("Test Package");
        existingPackage.setPiecesCarton(10);
        existingPackage.setAvailableQuantity(100);
        existingPackage.setPrice(50.0);
        Carton carton = new Carton();
        carton.setId(1L);
        existingPackage.setCartonId(carton);

        when(packageRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingPackage));
        assertThrows(ValidationException.class, () -> packageService.updatePackage(1L, packageDTO));
        verify(packageRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testUpdatePackage_InvalidAvailableQuantity() {
        PackageDTO packageDTO = new PackageDTO();
        packageDTO.setName("name");
        packageDTO.setPiecesCarton(11);
        packageDTO.setAvailableQuantity(-10);
        packageDTO.setPhoto("Photo");
        packageDTO.setPrice(100);
        packageDTO.setCartonId(1L);

        Long id = 1L;
        Package existingPackage = new Package();
        existingPackage.setId(id);
        existingPackage.setName("Test Package");
        existingPackage.setPiecesCarton(10);
        existingPackage.setAvailableQuantity(100);
        existingPackage.setPrice(50.0);
        Carton carton = new Carton();
        carton.setId(1L);
        existingPackage.setCartonId(carton);

        when(packageRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingPackage));
        assertThrows(ValidationException.class, () -> packageService.updatePackage(1L, packageDTO));
        verify(packageRepository, times(1)).findByIdAndDeletedFalse(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testDeletePackage_ExistingId() throws ChangeSetPersister.NotFoundException {
        Package existingPackage = new Package();
        existingPackage.setId(1L);
        existingPackage.setDeleted(false);
        when(packageRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingPackage));
        packageService.deletePackage(1L);
        verify(packageRepository, times(1)).findByIdAndDeletedFalse(1L);
    }

    @Test
    void testDeletePackage_NonExistingId() {
        when(packageRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> packageService.deletePackage(1L));
        verify(packageRepository, times(1)).findByIdAndDeletedFalse(1L);
    }
}
