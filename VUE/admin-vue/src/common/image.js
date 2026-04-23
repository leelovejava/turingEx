export const openImage = (img) => {
  console.log(img)
  const image = new Image();
  image.src = img;
  const newWindow = window.open();
  newWindow.document.body.appendChild(image);
}