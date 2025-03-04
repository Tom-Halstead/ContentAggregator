import com.contentaggregator.exception.ResourceNotFoundException;
import com.contentaggregator.model.NewsSource;
import com.contentaggregator.model.User;
import com.contentaggregator.model.UserNewsSource;
import com.contentaggregator.repository.NewsSourceRepository;
import com.contentaggregator.repository.UserNewsSourceRepository;
import com.contentaggregator.repository.UserRepository;
import com.contentaggregator.service.UserNewsSourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserNewsSourceServiceTest {

    @Mock
    private UserNewsSourceRepository userNewsSourceRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NewsSourceRepository newsSourceRepository;

    @InjectMocks
    private UserNewsSourceService userNewsSourceService;

    private User mockUser;
    private NewsSource mockNewsSource;
    private UserNewsSource mockUserNewsSource;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setUserId(1);
        mockUser.setUsername("testuser");

        mockNewsSource = new NewsSource();
        mockNewsSource.setId(100);
        mockNewsSource.setName("BBC News");

        mockUserNewsSource = new UserNewsSource(mockUser, mockNewsSource, "custom_params", LocalDateTime.now());
    }

    @Test
    void testGetUserSources_UserExists() {
        // Mock repository behavior
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(userNewsSourceRepository.findByUser(mockUser)).thenReturn(List.of(mockUserNewsSource));

        // Execute service method
        List<UserNewsSource> sources = userNewsSourceService.getUserSources(1);

        // Assertions
        assertNotNull(sources, "Returned list should not be null");
        assertFalse(sources.isEmpty(), "Returned list should not be empty");
        assertEquals(1, sources.size(), "List should contain exactly one item");
        assertEquals(mockNewsSource.getId(), sources.get(0).getNewsSource().getId(), "NewsSource ID should match");
        assertEquals(mockNewsSource.getName(), sources.get(0).getNewsSource().getName(), "NewsSource name should match");
        assertEquals(mockUser.getUserId(), sources.get(0).getUser().getUserId(), "User ID should match");

        // Verify interactions with mocks
        verify(userRepository, times(1)).findById(1);
        verify(userNewsSourceRepository, times(1)).findByUser(mockUser);
    }

    @Test
    void testGetUserSources_UserNotFound() {
        // Mock repository behavior (User ID not found)
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // Execute & Assert exception
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                userNewsSourceService.getUserSources(999)
        );

        assertEquals("User not found with ID: 999", exception.getMessage(), "Exception message should match expected output");

        // Verify interactions with mocks
        verify(userRepository, times(1)).findById(999);
        verify(userNewsSourceRepository, times(0)).findByUser(any());
    }

    @Test
    void testAddUserSource_Success() {
        // Mock repository behavior
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(newsSourceRepository.findById(100)).thenReturn(Optional.of(mockNewsSource));

        // Execute service method
        assertDoesNotThrow(() -> userNewsSourceService.addUserSource(1, 100, "customParams"),
                "Adding a valid source should not throw an exception");

        // Verify interactions with mocks
        verify(userRepository, times(1)).findById(1);
        verify(newsSourceRepository, times(1)).findById(100);
        verify(userNewsSourceRepository, times(1)).save(any(UserNewsSource.class));
    }

    @Test
    void testAddUserSource_UserNotFound() {
        // Mock repository behavior (User ID not found)
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // Execute & Assert exception
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                userNewsSourceService.addUserSource(999, 100, "customParams")
        );

        assertEquals("User not found with ID: 999", exception.getMessage(), "Exception message should match expected output");

        // Verify interactions with mocks
        verify(userRepository, times(1)).findById(999);
        verify(newsSourceRepository, times(0)).findById(any());
        verify(userNewsSourceRepository, times(0)).save(any());
    }

    @Test
    void testAddUserSource_NewsSourceNotFound() {
        // Mock repository behavior (User exists but NewsSource does not)
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(newsSourceRepository.findById(999)).thenReturn(Optional.empty());

        // Execute & Assert exception
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                userNewsSourceService.addUserSource(1, 999, "customParams")
        );

        assertEquals("News source not found with ID: 999", exception.getMessage(), "Exception message should match expected output");

        // Verify interactions with mocks
        verify(userRepository, times(1)).findById(1);
        verify(newsSourceRepository, times(1)).findById(999);
        verify(userNewsSourceRepository, times(0)).save(any());
    }
}