import { useEffect, useState } from 'react';
import { fileAPI } from '../../service/file-service';
import { FileInfo } from '../../model/file/FileInfo';
import { FileManager } from '@cubone/react-file-manager';
import '@cubone/react-file-manager/dist/style.css';
import ComponentCard from '../../components/common/ComponentCard';
import { SPRING_BASE_URL } from '../../utils/utils';

interface File {
  name: string;
  isDirectory: boolean;
  path: string;
  size: number;
  updatedAt: string;
}

export default function Home() {
  const [files, setDirectories] = useState<File[]>([]);
  const [currentPath] = useState<string>('');

  // fetch files when path changes
  useEffect(() => {
    const fetchFiles = async () => {
      fileAPI.getAllFiles(currentPath).then((res) => {
        seperateDirs(res.data);
      });
    };

    fetchFiles();
  }, [currentPath]);

  const fetchFiles = async () => {
    fileAPI.getAllFiles(currentPath).then((res) => {
      seperateDirs(res.data);
    });
  };

  const seperateDirs = (files: FileInfo[]) => {
    const dirs: File[] = [];

    files.forEach((file) => {
      const parts = file.path.split('/');
      parts.shift();

      parts.forEach((part, index) => {
        const path = parts.slice(0, index + 1).join('/');

        if (!dirs.find((d) => d.path === path)) {
          dirs.push({
            name: part,
            path: path,
            isDirectory: index + 1 === parts.length ? false : true,
            size: index + 1 === parts.length ? file.size : 0,
            updatedAt: file.updatedAt
          });
        }
      });
    });

    setDirectories(dirs);
  };

  const deleteFile = (path: string, recursive: boolean) => {
    fileAPI.deleteFile(path, recursive).then(() => {
      fetchFiles();
    });
  };

  return (
    <ComponentCard title="File Explorer">
      <FileManager
        primaryColor={'#465fff'}
        files={files}
        filePreviewPath={`${SPRING_BASE_URL}/files/preview`}
        onDelete={(files: Array<File>) => {
          files.forEach((file) => {
            deleteFile(file.path, false);
          });
        }}
      />
    </ComponentCard>
  );
}
