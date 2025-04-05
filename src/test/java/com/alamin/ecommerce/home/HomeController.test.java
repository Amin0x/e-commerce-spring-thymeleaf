@Test
void shouldReturnCorrectViewNameWhenRootUrlAccessed() {
    // Arrange
    HomeController homeController = new HomeController();
    Model model = org.mockito.Mockito.mock(Model.class);

    // Act
    String viewName = homeController.home(model);

    // Assert
    assertEquals("public/home", viewName);
}