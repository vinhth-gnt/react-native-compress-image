import { NativeModules } from 'react-native';
const ImageCompressAndroid = NativeModules.ImageCompressAndroid;

export default {
  createCompressedImage: (imagePath, directoryPath) => {
    return new Promise((resolve, reject) => {
      ImageCompressAndroid.createCompressedImage(imagePath, directoryPath, resolve, reject);
    });
  },
  createCustomCompressedImage: (imagePath, directoryPath, outputName, maxWidth, maxHeight, quality) => {
    return new Promise((resolve, reject) => {
      ImageCompressAndroid.createCustomCompressedImage(imagePath, directoryPath, outputName, maxWidth, maxHeight, quality, resolve, reject);
    });
  },
  caculateSize: (paths) => {
    return new Promise((resolve, reject) => {
      ImageCompressAndroid.caculateSize(paths, resolve, reject);
    });
  },
  resizeImage: (imagePath, directoryPath, outputName, ratio) => {
    return new Promise((resolve, reject) => {
      ImageCompressAndroid.resizeImage(imagePath, directoryPath, outputName, ratio, resolve, reject);
    });
  },
};